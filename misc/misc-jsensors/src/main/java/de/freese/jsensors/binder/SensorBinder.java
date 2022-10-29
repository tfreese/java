// Created: 02.09.2021
package de.freese.jsensors.binder;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * Binds {@link Sensor}s with one or more information to the {@link SensorRegistry}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SensorBinder
{
    void bindTo(SensorRegistry registry);
}
