// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.util.function.Function;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class SwapMetrics implements SensorBinder {
    private final JavaSysMon sysMon = new JavaSysMon();

    @Override
    public void bindTo(final SensorRegistry registry, Function<String, Backend> backendProvider) {
        Sensor.builder("swap.free", this.sysMon, mon -> {
            MemoryStats stats = mon.swap();

            return Long.toString(stats.getFreeBytes());
        }).description("Free swap in Bytes").register(registry, backendProvider);

        Sensor.builder("swap.usage", this.sysMon, mon -> {
            MemoryStats stats = mon.swap();
            double free = stats.getFreeBytes();
            double total = stats.getTotalBytes();
            double usage = (1D - (free / total)) * 100D;

            return Double.toString(usage);
        }).description("Used swap in %").register(registry, backendProvider);
    }
}
