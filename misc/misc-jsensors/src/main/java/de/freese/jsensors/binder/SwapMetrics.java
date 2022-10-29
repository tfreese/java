// Created: 02.09.2021
package de.freese.jsensors.binder;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class SwapMetrics implements SensorBinder
{
    /**
    *
    */
    private final JavaSysMon sysMon = new JavaSysMon();

    /**
     * @see de.freese.jsensors.binder.SensorBinder#bindTo(de.freese.jsensors.registry.SensorRegistry)
     */
    @Override
    public void bindTo(final SensorRegistry registry)
    {
        Sensor.builder("swap.free", this.sysMon, mon -> {
            MemoryStats stats = mon.swap();

            return Long.toString(stats.getFreeBytes());
        }).description("Free swap in Bytes").register(registry);

        Sensor.builder("swap.usage", this.sysMon, mon -> {
            MemoryStats stats = mon.swap();
            double free = stats.getFreeBytes();
            double total = stats.getTotalBytes();
            double usage = (1D - (free / total)) * 100D;

            return Double.toString(usage);
        }).description("Used swap in %").register(registry);
    }
}
