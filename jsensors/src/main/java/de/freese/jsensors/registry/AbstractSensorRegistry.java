// Created: 02.09.2021
package de.freese.jsensors.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.sensor.DefaultSensor;
import de.freese.jsensors.sensor.Sensor;

/**
 * Base Implementation of a {@link SensorRegistry}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSensorRegistry implements SensorRegistry {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Sensor> sensors = new HashMap<>();

    /**
     * @see de.freese.jsensors.registry.SensorRegistry#getSensor(java.lang.String)
     */
    @Override
    public Sensor getSensor(final String name) {
        Sensor sensor = this.sensors.get(name);

        if (sensor == null) {
            throw new IllegalStateException(String.format("sensor does not exist: '%s'", name));
        }

        return sensor;
    }

    /**
     * @see de.freese.jsensors.registry.SensorRegistry#getSensors()
     */
    @Override
    public Stream<Sensor> getSensors() {
        return this.sensors.values().stream();
    }

    /**
     * @see de.freese.jsensors.registry.SensorRegistry#newSensor(java.lang.String, java.lang.Object, java.util.function.Function, int, java.lang.String)
     */
    @Override
    public <T> Sensor newSensor(final String name, final T obj, final Function<T, String> function, final int keepLastNValues, final String description) {
        return register(name, () -> new DefaultSensor<>(name, obj, function, keepLastNValues, description));
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * Throws an IllegalStateException if SensorName already exist.
     */
    protected Sensor register(final String name, final Supplier<Sensor> supplier) {
        if (this.sensors.containsKey(name)) {
            throw new IllegalStateException(String.format("sensor already exist: '%s'", name));
        }

        return this.sensors.computeIfAbsent(name, key -> supplier.get());
    }
}
