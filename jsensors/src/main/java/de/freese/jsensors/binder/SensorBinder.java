package de.freese.jsensors.binder;

import java.util.Map;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * Binds {@link Sensor}s with one or more information to the {@link SensorRegistry}.
 *
 * @author Thomas Freese
 * @since 02.09.2021
 */
@FunctionalInterface
public interface SensorBinder {
    /**
     * @return Map of bounded Sensors.
     */
    Map<String, Sensor> bindTo(SensorRegistry registry);
}
