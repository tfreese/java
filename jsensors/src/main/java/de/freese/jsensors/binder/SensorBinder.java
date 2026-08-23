package de.freese.jsensors.binder;

import java.util.List;
import java.util.function.Function;

import de.freese.jsensors.backend.Backend;
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
     * @return List of bound Sensors.
     */
    List<String> bindTo(SensorRegistry registry, Function<String, Backend> backendProvider);
}
