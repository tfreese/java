// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Implementation for a {@link Sensor}.
 *
 * @param <T> Type of the object from which the value is extracted.
 *
 * @author Thomas Freese
 */
public class DefaultSensor<T> implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSensor.class);

    private final String description;
    private final String name;
    private final WeakReference<T> ref;
    private final Function<T, String> valueFunction;

    public DefaultSensor(final String name, final T obj, final Function<T, String> valueFunction, final String description) {
        super();

        this.name = Objects.requireNonNull(name, "name required");
        this.ref = new WeakReference<>(Objects.requireNonNull(obj, "obj required"));
        this.valueFunction = Objects.requireNonNull(valueFunction, "valueFunction required");
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SensorValue measure() {
        final T obj = ref.get();

        if (obj != null) {
            final String functionValue = valueFunction.apply(obj);

            return new DefaultSensorValue(getName(), functionValue, System.currentTimeMillis());
        }
        else {
            LOGGER.warn("no object for valueFunction exist for sensor: {}", getName());
        }

        return null;
    }
}
