// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.util.function.Function;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class CpuMetrics implements SensorBinder
{
    /**
    *
    */
    private final JavaSysMon sysMon = new JavaSysMon();

    /**
     * @param registry {@link SensorRegistry}
     */
    private void bindCpuUsage(final SensorRegistry registry)
    {
        Function<JavaSysMon, String> function = new Function<>()
        {
            /**
            *
            */
            private CpuTimes cpuTimesPrevious;

            /**
             * @see java.util.function.Function#apply(java.lang.Object)
             */
            @Override
            public String apply(final JavaSysMon t)
            {
                CpuTimes cpuTimes = CpuMetrics.this.sysMon.cpuTimes();

                if (this.cpuTimesPrevious == null)
                {
                    this.cpuTimesPrevious = cpuTimes;
                    return "";
                }

                double usage = cpuTimes.getCpuUsage(this.cpuTimesPrevious) * 100D;

                return Double.toString(usage);
            }
        };

        Sensor.builder("cpu.usage", this.sysMon, function).description("CPU-Usage in %").register(registry);
    }

    /**
     * @see de.freese.jsensors.binder.SensorBinder#bindTo(de.freese.jsensors.registry.SensorRegistry)
     */
    @Override
    public void bindTo(final SensorRegistry registry)
    {
        bindCpuUsage(registry);
    }
}
