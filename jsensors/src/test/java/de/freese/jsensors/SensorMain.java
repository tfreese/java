package de.freese.jsensors;

import static org.awaitility.Awaitility.await;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.CompositeBackend;
import de.freese.jsensors.backend.ConsoleBackend;
import de.freese.jsensors.backend.file.CsvBackend;
import de.freese.jsensors.binder.CpuMetrics;
import de.freese.jsensors.binder.MemoryMetrics;
import de.freese.jsensors.binder.SwapMetrics;
import de.freese.jsensors.registry.DefaultSensorRegistry;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.utils.JSensorThreadFactory;

/**
 * @author Thomas Freese
 * @since 31.10.2020
 */
public final class SensorMain {
    static void main() {
        final Path logPath = Paths.get(System.getProperty("user.home"), ".java-apps", "jSensors");

        try (ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2, new JSensorThreadFactory("scheduler-%d"))) {
            final SensorRegistry registry = new DefaultSensorRegistry();

            final ConsoleBackend consoleBackend = new ConsoleBackend();
            final CsvBackend csvBackendCpuUsage = new CsvBackend(5, logPath.resolve("cpuUsage.csv"), true);
            csvBackendCpuUsage.start();

            // CPU
            final Backend backendCpu = new CompositeBackend().add(consoleBackend).add(csvBackendCpuUsage);
            new CpuMetrics().bindTo(registry, name -> backendCpu);
            scheduledExecutorService.scheduleWithFixedDelay(() -> backendCpu.store(registry.getSensor("cpu.usage").measure()), 1, 1, TimeUnit.SECONDS);

            // Swap
            new SwapMetrics().bindTo(registry, name -> consoleBackend);
            scheduledExecutorService.scheduleWithFixedDelay(() -> consoleBackend.store(registry.getSensor("swap.free").measure()), 1, 1, TimeUnit.SECONDS);
            scheduledExecutorService.scheduleWithFixedDelay(() -> consoleBackend.store(registry.getSensor("swap.usage").measure()), 1, 1, TimeUnit.SECONDS);

            // Memory
            final CsvBackend csvBackendMemory = new CsvBackend(5, logPath.resolve("memoryMetrics.csv"), false);
            csvBackendMemory.start();

            final Backend backendMemory = new CompositeBackend().add(consoleBackend).add(csvBackendMemory);
            new MemoryMetrics().bindTo(registry, name -> backendMemory);

            scheduledExecutorService.scheduleWithFixedDelay(() -> backendMemory.store(registry.getSensor("memory.free").measure()), 1, 1, TimeUnit.SECONDS);
            scheduledExecutorService.scheduleWithFixedDelay(() -> backendMemory.store(registry.getSensor("memory.max").measure()), 1, 1, TimeUnit.SECONDS);
            scheduledExecutorService.scheduleWithFixedDelay(() -> backendMemory.store(registry.getSensor("memory.usage").measure()), 1, 1, TimeUnit.SECONDS);

            // TimeUnit.SECONDS.sleep(10L);
            await().pollDelay(Duration.ofSeconds(10L)).timeout(Duration.ofSeconds(11L)).until(() -> true);

            csvBackendMemory.stop(); // Trigger submit/commit
            csvBackendCpuUsage.stop(); // Trigger submit/commit
        }
    }

    private SensorMain() {
        super();
    }
}
