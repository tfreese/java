package de.freese.jsensors.binder;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.function.Function;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 * @since 02.09.2021
 */
public class CpuMetrics implements SensorBinder {
    private final com.sun.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        return bindCpuUsage(registry, backendProvider);
    }

    private List<String> bindCpuUsage(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        final Sensor usageSensor = Sensor.builder("cpu.usage", operatingSystemMXBean, bean -> Double.toString(bean.getCpuLoad() * 100D))
                .description("CPU-Usage in %").register(registry, backendProvider);

        return List.of(usageSensor.getName());
    }
}
