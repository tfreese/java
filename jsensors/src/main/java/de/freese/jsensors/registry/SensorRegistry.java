// Created: 02.09.2021
package de.freese.jsensors.registry;

import java.util.function.Function;
import java.util.stream.Stream;

import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public interface SensorRegistry
{
    /**
     * Throws an IllegalStateException if no {@link Sensor} exist for this Name.
     */
    Sensor getSensor(String name);

    Stream<Sensor> getSensors();

    <T> Sensor newSensor(String name, T obj, Function<T, String> valueFunction, int keepLastNValues, String description);
}
