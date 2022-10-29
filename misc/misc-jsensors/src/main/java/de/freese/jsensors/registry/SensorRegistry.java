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
     *
     * @param name String
     *
     * @return {@link Sensor}
     */
    Sensor getSensor(final String name);

    /**
     * @return {@link Stream}
     */
    Stream<Sensor> getSensors();

    /**
     * @param <T> Type of the object from which the value is extracted.
     * @param name String
     * @param obj Object
     * @param function {@link Function}
     * @param keepLastNValues int
     * @param description String
     *
     * @return {@link Sensor}
     */
    <T> Sensor newSensor(final String name, final T obj, final Function<T, String> function, int keepLastNValues, String description);
}
