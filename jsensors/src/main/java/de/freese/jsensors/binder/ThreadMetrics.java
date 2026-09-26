package de.freese.jsensors.binder;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 * @since 02.09.2021
 */
public class ThreadMetrics implements SensorBinder {
    @Override
    public Map<String, Sensor> bindTo(final SensorRegistry registry) {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        final Sensor countSensor = Sensor.builder("thread.count", threadMXBean, bean ->
                        Integer.toString(bean.getThreadCount())
                )
                .description("Thread count")
                .register(registry);

        return Map.of(countSensor.getName(), countSensor);
    }
}
