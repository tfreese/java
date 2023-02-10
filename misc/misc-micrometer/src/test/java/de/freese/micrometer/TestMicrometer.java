// Created: 08.04.2021
package de.freese.micrometer;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.LongTaskTimer.Sample;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
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
        List<String> list = new ArrayList<>(4);

        Gauge gauge = Gauge.builder("cache.size", list, List::size).register(Metrics.globalRegistry);

        assertEquals(0.0D, gauge.value());

        list.add("1");

        assertEquals(1.0D, gauge.value());
        assertEquals(1.0D, Metrics.globalRegistry.find("cache.size").gauge().value());
    }

    /**
     * Siehe io.micrometer.core.instrument.logging.LoggingMeterRegistry.publish()
     */
    @Test
    @Order(10)
    void testIterateMeters() throws Exception {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        Counter.builder("test.counter").register(meterRegistry).increment(1.0D);
        Gauge.builder("test.gauge", Math::random).register(meterRegistry).value();
        Timer.builder("test.timer").register(meterRegistry).record(Duration.ofMillis(100));

        Sample sample = LongTaskTimer.builder("test.longTaskTimer").register(meterRegistry).start();
        TimeUnit.SECONDS.sleep(1);
        sample.stop();

        meterRegistry.forEachMeter(meter -> System.out.println(writeMeter(meter)));

        System.out.println();

        Set<String> meterExport = new TreeSet<>();

        Metrics.globalRegistry.getRegistries().forEach(registry -> {
            // Hier können Duplikate durch verschiedene Registries entstehen.
            MeterExporter.export(registry, Duration.ofSeconds(1), TimeUnit.MILLISECONDS).forEach(meterExport::add);
        });

        meterExport.forEach(System.out::println);

        assertTrue(true);
    }

    @Test
    @Order(3)
    void testPushMetrics() throws Exception {
        // PushRegistryConfig
        LoggingRegistryConfig loggingRegistryConfig = new LoggingRegistryConfig() {
            @Override
            public String get(final String key) {
                return null;
            }

            /**
             * @see io.micrometer.core.instrument.push.PushRegistryConfig#step()
             */
            @Override
            public Duration step() {
                return Duration.ofSeconds(1);
            }
        };

        PushMeterRegistry pushMeterRegistry = new LoggingMeterRegistry(loggingRegistryConfig, Clock.SYSTEM);
        Metrics.addRegistry(pushMeterRegistry);

        pushMeterRegistry.start(Executors.defaultThreadFactory());

        Counter.builder("test.counter").register(pushMeterRegistry).increment(1.0D);
        Gauge.builder("test.gauge", Math::random).register(pushMeterRegistry).value();
        Timer.builder("test.timer").register(pushMeterRegistry).record(Duration.ofMillis(100));

        Sample sample = LongTaskTimer.builder("test.longTaskTimer").register(pushMeterRegistry).start();
        TimeUnit.SECONDS.sleep(1);
        sample.stop();

        TimeUnit.MILLISECONDS.sleep(1500);

        pushMeterRegistry.stop();

        assertTrue(true);
    }

    @Test
    @Order(4)
    void testTimer() {
        // Short Task Timer
        Timer timer = Metrics.timer("app.event");

        timer.record(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1500);
            }
            catch (InterruptedException ex) {
                // Empty
            }
        });

        timer.record(3000, TimeUnit.MILLISECONDS);

        assertEquals(2, timer.count());
        assertEquals(2, Metrics.globalRegistry.find("app.event").timer().count());
        assertTrue((4510 > timer.totalTime(TimeUnit.MILLISECONDS)) && (4500 <= timer.totalTime(TimeUnit.MILLISECONDS)));

        // Long Task Timer
        LongTaskTimer longTaskTimer = LongTaskTimer.builder("3rdPartyService").register(Metrics.globalRegistry);

        Sample sample = longTaskTimer.start();

        try {
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException ex) {
            // Empty
        }

        long timeElapsed = sample.stop();

        assertEquals(2, (timeElapsed / (int) 1e9));
        assertNotNull(Metrics.globalRegistry.find("3rdPartyService").longTaskTimer());
    }

    String writeMeter(final Meter meter) {
        return StreamSupport.stream(meter.measure().spliterator(), false).map(ms -> {
            String msLine = ms.getStatistic().getTagValueRepresentation() + "=";

            return switch (ms.getStatistic()) {
                case COUNT -> "value=" + ms.getValue();
                default -> msLine + ms.getValue();
            };
        }).collect(joining(", ", meter.getId() + " ", ""));
    }
}
