// Created: 31.10.2020
package de.freese.jsensors;

import static org.awaitility.Awaitility.await;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import de.freese.jsensors.backend.CompositeBackend;
import de.freese.jsensors.backend.ConsoleBackend;
import de.freese.jsensors.backend.file.CsvBackend;
import de.freese.jsensors.binder.CpuMetrics;
import de.freese.jsensors.binder.MemoryMetrics;
import de.freese.jsensors.binder.SwapMetrics;
import de.freese.jsensors.registry.ScheduledSensorRegistry;
import de.freese.jsensors.utils.JSensorThreadFactory;

/**
 * @author Thomas Freese
 */
public final class SensorMain {
    static void main() {
        final Path logPath = Paths.get(System.getProperty("user.home"), ".java-apps", "jSensors");

        final ScheduledSensorRegistry registry = new ScheduledSensorRegistry(new JSensorThreadFactory("scheduler-%d"), 4);
        registry.start();

        final ConsoleBackend consoleBackend = new ConsoleBackend();
        final CsvBackend csvBackendCpuUsage = new CsvBackend(5, logPath.resolve("cpuUsage.csv"), true);
        csvBackendCpuUsage.start();

        // CPU
        new CpuMetrics().bindTo(registry, name -> new CompositeBackend().add(consoleBackend).add(csvBackendCpuUsage));
        registry.scheduleSensor("cpu.usage", 1, 1, TimeUnit.SECONDS);

        // Swap
        new SwapMetrics().bindTo(registry, name -> consoleBackend);
        registry.scheduleSensor("swap.free", 1, 1, TimeUnit.SECONDS);
        registry.scheduleSensor("swap.usage", 1, 1, TimeUnit.SECONDS);

        // Memory
        final CsvBackend csvBackendMemory = new CsvBackend(5, logPath.resolve("memoryMetrics.csv"), false);
        csvBackendMemory.start();

        new MemoryMetrics().bindTo(registry, name -> new CompositeBackend().add(consoleBackend).add(csvBackendMemory));

        registry.scheduleSensor("memory.free", 1, 1, TimeUnit.SECONDS);
        registry.scheduleSensor("memory.max", 1, 1, TimeUnit.SECONDS);
        registry.scheduleSensor("memory.total", 1, 1, TimeUnit.SECONDS);
        registry.scheduleSensor("memory.usage", 1, 1, TimeUnit.SECONDS);

        // TimeUnit.SECONDS.sleep(10L);
        await().pollDelay(Duration.ofSeconds(10L)).timeout(Duration.ofSeconds(11L)).until(() -> true);

        csvBackendMemory.stop(); // Trigger submit/commit
        csvBackendCpuUsage.stop(); // Trigger submit/commit
        registry.stop();
    }

    private SensorMain() {
        super();
    }
}
