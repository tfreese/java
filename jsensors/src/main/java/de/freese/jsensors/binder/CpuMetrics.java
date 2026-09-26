package de.freese.jsensors.binder;

import java.lang.management.ManagementFactory;
import java.util.Map;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 * @since 02.09.2021
 */
public class CpuMetrics implements SensorBinder {
    @Override
    public Map<String, Sensor> bindTo(final SensorRegistry registry) {
        final com.sun.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

        final Sensor usageSensor = Sensor.builder("cpu.usage", operatingSystemMXBean, bean ->
                        Double.toString(bean.getCpuLoad() * 100D)
                )
                .description("CPU-Usage in %")
                .register(registry);

        return Map.of(usageSensor.getName(), usageSensor);
    }
}
