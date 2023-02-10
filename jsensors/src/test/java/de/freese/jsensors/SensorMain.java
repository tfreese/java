// Created: 31.10.2020
package de.freese.jsensors;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    static final Logger LOGGER = LoggerFactory.getLogger(SensorMain.class);

    public static void main(final String[] args) throws Exception {
        Path logPath = Paths.get(System.getProperty("user.home"), ".java-apps", "jSensors");

        ScheduledSensorRegistry registry = new ScheduledSensorRegistry(new JSensorThreadFactory("scheduler"), 4);
        registry.start();

        ConsoleBackend consoleBackend = new ConsoleBackend();

        // CPU
        new CpuMetrics().bindTo(registry);

        CsvBackend csvBackendCpuUsage = new CsvBackend(logPath.resolve("cpuUsage.csv"), true, 5);
        csvBackendCpuUsage.start();

        registry.scheduleSensor("cpu.usage", 1, 1, TimeUnit.SECONDS, new CompositeBackend().add(consoleBackend).add(csvBackendCpuUsage));

        // Swap
        new SwapMetrics().bindTo(registry);

        registry.scheduleSensor("swap.free", 1, 1, TimeUnit.SECONDS, consoleBackend);
        registry.scheduleSensor("swap.usage", 1, 1, TimeUnit.SECONDS, consoleBackend);

        // Memory
        new MemoryMetrics().bindTo(registry);

        CsvBackend csvBackendMemory = new CsvBackend(logPath.resolve("memoryMetrics.csv"), false, 6);
        csvBackendMemory.start();

        registry.scheduleSensor("memory.free", 1, 1, TimeUnit.SECONDS, new CompositeBackend().add(consoleBackend).add(csvBackendMemory));
        registry.scheduleSensor("memory.max", 1, 1, TimeUnit.SECONDS, new CompositeBackend().add(consoleBackend).add(csvBackendMemory));
        registry.scheduleSensor("memory.total", 1, 1, TimeUnit.SECONDS, new CompositeBackend().add(consoleBackend).add(csvBackendMemory));
        registry.scheduleSensor("memory.usage", 1, 1, TimeUnit.SECONDS, new CompositeBackend().add(consoleBackend).add(csvBackendMemory));

        TimeUnit.SECONDS.sleep(10);

        csvBackendMemory.stop(); // Trigger submit/commit
        csvBackendCpuUsage.stop(); // Trigger submit/commit
        registry.stop();
    }

    private SensorMain() {
        super();
    }
}
