// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.function.Function;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class SwapMetrics implements SensorBinder {
    private final com.sun.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        Sensor.builder("swap.free", operatingSystemMXBean, bean -> Long.toString(bean.getFreeSwapSpaceSize())).description("Free swap in Bytes")
                .register(registry, backendProvider);

        Sensor.builder("swap.total", operatingSystemMXBean, bean -> Long.toString(bean.getTotalMemorySize())).description("Total swap in Bytes")
                .register(registry, backendProvider);

        Sensor.builder("swap.usage", operatingSystemMXBean, bean -> {
            final long free = bean.getFreeSwapSpaceSize();
            final long total = bean.getTotalMemorySize();
            final double usage = ((double) free / total) * 100D;

            return Double.toString(usage);
        }).description("Used swap in %").register(registry, backendProvider);

        return List.of("swap.free", "swap.total", "swap.usage");
    }
}
