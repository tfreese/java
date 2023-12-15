// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.util.List;
import java.util.function.Function;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class MemoryMetrics implements SensorBinder {
    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        final Runtime runtime = Runtime.getRuntime();

        Sensor.builder("memory.free", runtime, r -> Long.toString(r.freeMemory())).description("Free memory in Bytes").register(registry, backendProvider);
        Sensor.builder("memory.max", runtime, r -> Long.toString(r.maxMemory())).description("Max. memory in Bytes").register(registry, backendProvider);
        Sensor.builder("memory.total", runtime, r -> Long.toString(r.totalMemory())).description("Total memory in Bytes").register(registry, backendProvider);
        Sensor.builder("memory.usage", runtime, r -> {
            final double free = r.freeMemory();
            final double total = r.totalMemory();
            final double usage = (1D - (free / total)) * 100D;

            return Double.toString(usage);
        }).description("Used Memory in %").register(registry, backendProvider);

        return List.of("memory.free", "memory.max", "memory.total", "memory.usage");
    }
}
