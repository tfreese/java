// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.function.Function;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class MemoryMetrics implements SensorBinder {
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        final MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();

        Sensor.builder("memory.free", memoryUsage, mu -> Long.toString(mu.getMax() > 0 ? mu.getMax() : mu.getCommitted()))
                .description("Free memory in Bytes").register(registry, backendProvider);

        Sensor.builder("memory.max", memoryUsage, mu -> Long.toString(mu.getMax() > 0 ? mu.getMax() : mu.getCommitted()))
                .description("Max. memory in Bytes")
                .register(registry, backendProvider);

        Sensor.builder("memory.usage", memoryUsage, mu -> {
            final long used = mu.getUsed();
            final long max = mu.getMax() > 0 ? mu.getMax() : mu.getCommitted();

            return Double.toString(((double) used / max) * 100D);
        }).description("Used Memory in %").register(registry, backendProvider);

        return List.of("memory.free", "memory.max", "memory.usage");
    }
}
