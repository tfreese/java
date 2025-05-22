// Created: 08.04.2021
package de.freese.micrometer;

import static java.util.stream.Collectors.joining;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.LongTaskTimer.Sample;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestMicrometer {
    @AfterAll
    static void afterAll() {
        Metrics.globalRegistry.clear();
        Metrics.globalRegistry.close();
    }

    @BeforeAll
    static void beforeAll() {
        Metrics.addRegistry(new SimpleMeterRegistry());
    }

    @Test
    @Order(1)
    void testCounter() {
        final class CountedObject {
            private CountedObject() {
                super();

                Metrics.counter("objects.instance").increment(1.0D);
            }
        }

        Metrics.counter("objects.instance").increment();
        new CountedObject();

        Counter counter = Metrics.globalRegistry.find("objects.instance").counter();

        assertNotNull(counter);
        assertEquals(2.0D, counter.count());

        counter = Counter.builder("counter.instance").description("indicates instance count of the object").tags("dev", "performance").register(Metrics.globalRegistry);

        counter.increment(2D);

        assertEquals(2D, counter.count());

        counter.increment(-1D);

        assertEquals(1D, counter.count());
    }

    @Test
    @Order(2)
    void testGauge() {
        final List<String> list = new ArrayList<>(4);

        final Gauge gauge = Gauge.builder("cache.size", list, List::size).register(Metrics.globalRegistry);

        assertEquals(0.0D, gauge.value());

        list.add("1");

        assertEquals(1.0D, gauge.value());
    }

    /**
     * Siehe io.micrometer.core.instrument.logging.LoggingMeterRegistry.publish()
     */
    @Test
    @Order(10)
    void testIterateMeters() {
        Counter.builder("test.counter").register(Metrics.globalRegistry).increment(1.0D);
        Gauge.builder("test.gauge", Math::random).register(Metrics.globalRegistry).value();
        Timer.builder("test.timer").register(Metrics.globalRegistry).record(Duration.ofMillis(100L));

        final Sample sample = LongTaskTimer.builder("test.longTaskTimer").register(Metrics.globalRegistry).start();
        await().pollDelay(Duration.ofMillis(1000L)).until(() -> true);
        sample.stop();

        Metrics.globalRegistry.forEachMeter(meter -> System.out.println(writeMeter(meter)));

        System.out.println();

        final Set<String> meterExport = new TreeSet<>();

        Metrics.globalRegistry.getRegistries().forEach(registry -> {
            // Hier können Duplikate durch verschiedene Registries entstehen.
            meterExport.addAll(MeterExporter.export(registry, Duration.ofSeconds(1L), TimeUnit.MILLISECONDS));
        });

        meterExport.forEach(System.out::println);

        assertFalse(meterExport.isEmpty());
    }

    @Test
    @Order(3)
    void testPushMetrics() {
        final Logger logger = LoggerFactory.getLogger("LoggingRegistry");

        // PushRegistryConfig
        final LoggingRegistryConfig loggingRegistryConfig = new LoggingRegistryConfig() {
            @Override
            public String get(final String key) {
                return null;
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(1L);
            }
        };

        final PushMeterRegistry pushMeterRegistry = new LoggingMeterRegistry(loggingRegistryConfig, Clock.SYSTEM, logger::info);
        Metrics.addRegistry(pushMeterRegistry);

        // pushMeterRegistry.start(Executors.defaultThreadFactory());

        Counter.builder("test.counter").register(pushMeterRegistry).increment(1.0D);
        Gauge.builder("test.gauge", Math::random).register(pushMeterRegistry).value();
        Timer.builder("test.timer").register(pushMeterRegistry).record(Duration.ofMillis(100L));

        final Sample sample = LongTaskTimer.builder("test.longTaskTimer").register(pushMeterRegistry).start();
        await().pollDelay(Duration.ofSeconds(1L)).until(() -> true);
        sample.stop();

        await().pollDelay(Duration.ofSeconds(1L)).until(() -> true);

        pushMeterRegistry.stop();
        Metrics.removeRegistry(pushMeterRegistry);

        assertFalse(Metrics.globalRegistry.isClosed());
    }

    @Test
    @Order(4)
    void testTimer() throws Exception {
        // Short Task Timer
        final Timer timer = Metrics.timer("app.event");

        final Callable<Void> callable = () -> {
            await().pollDelay(Duration.ofMillis(100L)).until(() -> true);
            return null;
        };

        timer.record(100, TimeUnit.MILLISECONDS);
        timer.recordCallable(callable);

        assertEquals(2L, timer.count());
        assertNotNull(Metrics.globalRegistry.find("app.event"));
        assertEquals(2L, timer.count());
        assertEquals(200L, timer.totalTime(TimeUnit.MILLISECONDS), 20);

        // Long Task Timer
        final LongTaskTimer longTaskTimer = LongTaskTimer.builder("3rdPartyService").register(Metrics.globalRegistry);

        final Sample sample = longTaskTimer.start();

        await().pollDelay(Duration.ofMillis(1000L)).until(() -> true);

        final long durationInNanos = sample.stop();

        assertEquals(1_000_000_000L, durationInNanos, 10_000_000L);
        assertNotNull(Metrics.globalRegistry.find("3rdPartyService").longTaskTimer());
    }

    String writeMeter(final Meter meter) {
        return StreamSupport.stream(meter.measure().spliterator(), false).map(ms -> {
            final String msLine = ms.getStatistic().getTagValueRepresentation() + "=";

            return switch (ms.getStatistic()) {
                case COUNT -> "value=" + ms.getValue();
                default -> msLine + ms.getValue();
            };
        }).collect(joining(", ", meter.getId() + " ", ""));
    }
}
