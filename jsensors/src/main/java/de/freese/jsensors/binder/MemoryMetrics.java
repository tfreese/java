// Created: 02.09.2021
package de.freese.jsensors.binder;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class MemoryMetrics implements SensorBinder {
    /**
     * @see de.freese.jsensors.binder.SensorBinder#bindTo(de.freese.jsensors.registry.SensorRegistry)
     */
    @Override
    public void bindTo(final SensorRegistry registry) {
        final Runtime runtime = Runtime.getRuntime();

        Sensor.builder("memory.free", runtime, r -> Long.toString(r.freeMemory())).description("Free memory in Bytes").register(registry);
        Sensor.builder("memory.max", runtime, r -> Long.toString(r.maxMemory())).description("Max. memory in Bytes").register(registry);
        Sensor.builder("memory.total", runtime, r -> Long.toString(r.totalMemory())).description("Total memory in Bytes").register(registry);
        Sensor.builder("memory.usage", runtime, r -> {
            double free = r.freeMemory();
            double total = r.totalMemory();
            double usage = (1D - (free / total)) * 100D;

            return Double.toString(usage);
        }).description("Used Memory in %").register(registry);
    }
}
