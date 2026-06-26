// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.function.Function;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class ThreadMetrics implements SensorBinder {
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        return bindCpuUsage(registry, backendProvider);
    }

    private List<String> bindCpuUsage(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        Sensor.builder("thread.count", threadMXBean, bean -> Integer.toString(bean.getThreadCount()))
                .description("Thread count").register(registry, backendProvider);

        return List.of("thread.count");
    }
}
