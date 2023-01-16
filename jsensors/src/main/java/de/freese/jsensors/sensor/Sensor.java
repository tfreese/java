// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import de.freese.jsensors.registry.SensorRegistry;

/**
 * @author Thomas Freese
 */
public interface Sensor
{
    /**
     * @param <T> Type of the object from which the value is extracted.
     *
     * @author Thomas Freese
     */
    final class Builder<T>
    {
        private final String name;
        private final T obj;
        private final Function<T, String> valueFunction;
        private String description;

        private int keepLastNValues = 1;

        private Builder(final String name, final T obj, final Function<T, String> valueFunction)
        {
            super();

            this.name = Objects.requireNonNull(name, "name required");
            this.obj = Objects.requireNonNull(obj, "obj required");
            this.valueFunction = Objects.requireNonNull(valueFunction, "valueFunction required");
        }

        public Builder<T> description(final String description)
        {
            this.description = description;

            return this;
        }

        public Builder<T> keepLastNValues(final int keepLastNValues)
        {
            this.keepLastNValues = keepLastNValues;

            return this;
        }

        public Sensor register(final SensorRegistry registry)
        {
            if (this.keepLastNValues < 1)
            {
                throw new IllegalArgumentException("keepLastNValues < 1: " + this.keepLastNValues);
            }

            return registry.newSensor(this.name, this.obj, this.valueFunction, this.keepLastNValues, this.description);
        }
    }

    static <T> Builder<T> builder(final String name, final T obj, final Function<T, String> valueFunction)
    {
        return new Builder<>(name, obj, valueFunction);
    }

    String getDescription();

    String getName();

    SensorValue getValueLast();

    List<SensorValue> getValues();

    /**
     * Determine the next Sensor Value.
     */
    SensorValue measure();
}
