package de.freese.jsensors.binder;

import java.lang.management.ManagementFactory;
import java.util.Map;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 * @since 02.09.2021
 */
public class SwapMetrics implements SensorBinder {
    @Override
    public Map<String, Sensor> bindTo(final SensorRegistry registry) {
        final com.sun.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

        final Sensor freeSensor = Sensor.builder("swap.free", operatingSystemMXBean, bean ->
                        Long.toString(bean.getFreeSwapSpaceSize())
                )
                .description("Free swap in Bytes")
                .register(registry);

        final Sensor totalSensor = Sensor.builder("swap.total", operatingSystemMXBean, bean ->
                        Long.toString(bean.getTotalMemorySize())
                )
                .description("Total swap in Bytes")
                .register(registry);

        final Sensor usageSensor = Sensor.builder("swap.usage", operatingSystemMXBean, bean -> {
            final long free = bean.getFreeSwapSpaceSize();
            final long total = bean.getTotalMemorySize();
            final double usage = ((double) free / total) * 100D;

            return Double.toString(usage);
        }).description("Used swap in %").register(registry);

        return Map.of(
                freeSensor.getName(), freeSensor,
                totalSensor.getName(), totalSensor,
                usageSensor.getName(), usageSensor
        );
    }
}
