// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.util.List;
import java.util.function.Function;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class CpuMetrics implements SensorBinder {
    private final JavaSysMon sysMon = new JavaSysMon();

    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        return bindCpuUsage(registry, backendProvider);
    }

    private List<String> bindCpuUsage(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        final Function<JavaSysMon, String> valueFunction = new Function<>() {
            private CpuTimes cpuTimesPrevious;

            @Override
            public String apply(final JavaSysMon t) {
                final CpuTimes cpuTimes = CpuMetrics.this.sysMon.cpuTimes();

                if (this.cpuTimesPrevious == null) {
                    this.cpuTimesPrevious = cpuTimes;
                    return "0";
                }

                final double usage = cpuTimes.getCpuUsage(this.cpuTimesPrevious) * 100D;

                return Double.toString(usage);
            }
        };

        Sensor.builder("cpu.usage", this.sysMon, valueFunction).description("CPU-Usage in %").register(registry, backendProvider);

        return List.of("cpu.usage");
    }
}
