// Created: 08.04.2021
package de.freese.micrometer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import io.micrometer.core.instrument.simple.CountingMode;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusRenameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.micrometer.binder.NetworkMetrics;

/**
 * @author Thomas Freese
 */
public final class MicrometerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(MicrometerMain.class);

    public static void main(final String[] args) throws Exception {
        // initSimpleRegistry();
        initPrometheusRegistry();
        // initLoggingRegistry();

        Metrics.globalRegistry.config()
                // .meterFilter(MeterFilter.denyNameStartsWith("executor.pool.max"))
                // .meterFilter(MeterFilter.denyNameStartsWith("executor.queue.remaining"))
                .meterFilter(new MeterFilter() {
                    @Override
                    public MeterFilterReply accept(final Id id) {
                        if ("scheduledExecutorService".equals(id.getTag("name"))) {
                            if ("executor.pool.max".equals(id.getName()) || "executor.queue.remaining".equals(id.getName())) {
                                // Ist bei ScheduledExecutorService immer Integer.MAX_VALUE;
                                return MeterFilterReply.DENY;
                            }

                            return MeterFilterReply.ACCEPT;
                        }

                        return MeterFilterReply.NEUTRAL;
                    }
                })
        ;

        startMetrics();

        // Avoid Terminating
        // System.in.read();
    }

    static void initLoggingRegistry() {
        // PushRegistryConfig
        final LoggingRegistryConfig loggingRegistryConfig = new LoggingRegistryConfig() {
            @Override
            public String get(final String key) {
                return null;
            }

            @Override
            public Duration step() {
                // Default = 1 Minute
                return Duration.ofSeconds(1);
            }
        };

        final LoggingMeterRegistry loggingMeterRegistry = new LoggingMeterRegistry(loggingRegistryConfig, Clock.SYSTEM);
        // final LoggingMeterRegistry loggingMeterRegistry = LoggingMeterRegistry.builder(loggingRegistryConfig).clock(Clock.SYSTEM).loggingSink(System.out::println).build();
        Metrics.addRegistry(loggingMeterRegistry);
    }

    static void initPrometheusRegistry() throws Exception {
        // PrometheusConfig.DEFAULT; step = 1 Minute
        final PrometheusConfig prometheusConfig = new PrometheusConfig() {
            @Override
            public String get(final String key) {
                return null;
            }

            @Override
            public Duration step() {
                // Default = 1 Minute
                return Duration.ofSeconds(1);
            }
        };

        final PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(prometheusConfig);
        prometheusMeterRegistry.config().meterFilter(new PrometheusRenameFilter());
        Metrics.addRegistry(prometheusMeterRegistry);

        startServerForPrometheus();
    }

    static void initSimpleRegistry() {
        final SimpleConfig simpleConfig = new SimpleConfig() {
            @Override
            public String get(final String key) {
                return null;
            }

            @Override
            public CountingMode mode() {
                return CountingMode.STEP;
            }

            @Override
            public Duration step() {
                // Default = 1 Minute
                return Duration.ofSeconds(1);
            }
        };

        final SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry(simpleConfig, Clock.SYSTEM);
        Metrics.addRegistry(simpleMeterRegistry);
    }

    private static void startMetrics() {
        // new ClassLoaderMetrics().bindTo(Metrics.globalRegistry);
        // new JvmMemoryMetrics().bindTo(Metrics.globalRegistry);
        // new JvmGcMetrics().bindTo(Metrics.globalRegistry);
        // new ProcessorMetrics().bindTo(Metrics.globalRegistry);
        // new JvmThreadMetrics().bindTo(Metrics.globalRegistry);
        // new LogbackMetrics().bindTo(Metrics.globalRegistry);
        new NetworkMetrics().bindTo(Metrics.globalRegistry);

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4, new NamedThreadFactory("scheduler"));
        new ExecutorServiceMetrics(scheduledExecutorService, "scheduledExecutorService", null).bindTo(Metrics.globalRegistry);

        // Diese funktionieren nicht, da sie in privaten Wrappern gekapselt werden !
        // new ExecutorServiceMetrics(Executors.newSingleThreadExecutor(), "test1", null).bindTo(Metrics.globalRegistry);
        // new ExecutorServiceMetrics(Executors.newSingleThreadScheduledExecutor(), "test1", null).bindTo(Metrics.globalRegistry);

        // final ScheduledExecutorService scheduledExecutorService =
        // ExecutorServiceMetrics.monitor(Metrics.globalRegistry, Executors.newScheduledThreadPool(4, new NamedThreadFactory("scheduler")),
        // "scheduledExecutorService");

        // Gauge.builder("test.gauge", Math::random).register(Metrics.globalRegistry);

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            // networkMetrics.update();
            //
            // Metrics.counter("test.counter").increment();
            //
            // Metrics.timer("test.timer").record(() -> {
            // try {
            // TimeUnit.MILLISECONDS.sleep((int) (1500 * Math.random()));
            // }
            // catch (InterruptedException ex) {
            // // Empty
            // }
            // });
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * curl http://localhost:8080/prometheus<br>
     * Siehe auch <a href="https://github.com/prometheus/client_java/tree/master/simpleclient_httpserver">simpleclient_httpserver</a><br>
     * &lt;dependency&gt;io.prometheus:simpleclient_httpserver&lt;/dependency&gt;<br>
     */
    private static void startServerForPrometheus() throws Exception {
        final Optional<PrometheusMeterRegistry> prometheusMeterRegistryOptional = Metrics.globalRegistry.getRegistries().stream()
                .filter(PrometheusMeterRegistry.class::isInstance)
                .map(PrometheusMeterRegistry.class::cast)
                .findFirst();

        if (prometheusMeterRegistryOptional.isEmpty()) {
            return;
        }

        final PrometheusMeterRegistry prometheusMeterRegistry = prometheusMeterRegistryOptional.get();

        final HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(Executors.newSingleThreadExecutor(new NamedThreadFactory("jre-httpserver")));

        server.createContext("/prometheus", httpExchange -> {
            final String response = prometheusMeterRegistry.scrape();

            LOGGER.debug("{}{}", System.lineSeparator(), response);

            final byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(bytes);

                os.flush();
            }
        });

        server.createContext("/exporter", httpExchange -> {
            final String response = MeterExporter.export(prometheusMeterRegistry, Duration.ofSeconds(1), TimeUnit.SECONDS).stream()
                    .collect(Collectors.joining(System.lineSeparator()));

            LOGGER.debug("{}{}", System.lineSeparator(), response);

            final byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(bytes);

                os.flush();
            }
        });

        server.start();
        // new Thread(server::start).start();
    }

    private MicrometerMain() {
        super();
    }
}
