// Created: 02.09.2021
package de.freese.jsensors.binder;

import java.util.function.Function;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * Binds {@link Sensor}s with one or more information to the {@link SensorRegistry}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SensorBinder {
    void bindTo(SensorRegistry registry, Function<String, Backend> backendProvider);
}
