// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.util.List;
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
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        Sensor.builder("swap.free", sysMon, mon -> {
            final MemoryStats stats = mon.swap();

            return Long.toString(stats.getFreeBytes());
        }).description("Free swap in Bytes").register(registry, backendProvider);

        Sensor.builder("swap.usage", sysMon, mon -> {
            final MemoryStats stats = mon.swap();
            final double free = stats.getFreeBytes();
            final double total = stats.getTotalBytes();
            final double usage = (1D - (free / total)) * 100D;

            return Double.toString(usage);
        }).description("Used swap in %").register(registry, backendProvider);

        return List.of("swap.free", "swap.usage");
    }
}
