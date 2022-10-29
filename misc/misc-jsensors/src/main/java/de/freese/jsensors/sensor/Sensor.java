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
    class Builder<T>
    {
        /**
         *
         */
        private final Function<T, String> function;
        /**
         *
         */
        private final String name;
        /**
         *
         */
        private final T obj;
        /**
         *
         */
        private String description;
        /**
         *
         */
        private int keepLastNValues = 1;

        /**
         * Erstellt ein neues {@link Builder} Object.
         *
         * @param name String
         * @param obj Object
         * @param function {@link Function}
         */
        private Builder(final String name, final T obj, final Function<T, String> function)
        {
            super();

            this.name = Objects.requireNonNull(name, "name required");
            this.obj = Objects.requireNonNull(obj, "obj required");
            this.function = Objects.requireNonNull(function, "function required");
        }

        /**
         * @param description String
         *
         * @return {@link Builder}
         */
        public Builder<T> description(final String description)
        {
            this.description = description;

            return this;
        }

        /**
         * Keeps the last N-Values for this {@link Sensor}.
         *
         * @param keepLastNValues int
         *
         * @return {@link Builder}
         *
         * @see Sensor#getValues()
         */
        public Builder<T> keepLastNValues(final int keepLastNValues)
        {
            this.keepLastNValues = keepLastNValues;

            return this;
        }

        /**
         * @param registry {@link SensorRegistry}
         *
         * @return {@link Sensor}
         */
        public Sensor register(final SensorRegistry registry)
        {
            if (this.keepLastNValues < 1)
            {
                throw new IllegalArgumentException("keepLastNValues < 1: " + this.keepLastNValues);
            }

            return registry.newSensor(this.name, this.obj, this.function, this.keepLastNValues, this.description);
        }
    }

    /**
     * @param <T> Type of the object from which the value is extracted.
     * @param name String
     * @param obj Objects
     * @param function {@link Function}
     *
     * @return {@link Builder}
     */
    static <T> Builder<T> builder(final String name, final T obj, final Function<T, String> function)
    {
        return new Builder<>(name, obj, function);
    }

    /**
     * @return String
     */
    String getDescription();

    /**
     * @return String
     */
    String getName();

    /**
     * Returns the last {@link SensorValue}.
     *
     * @return {@link SensorValue}
     */
    SensorValue getValueLast();

    /**
     * Returns all of the {@link SensorValue}s.
     *
     * @return {@link SensorValue}
     *
     * @see Builder#keepLastNValues(int)
     */
    List<SensorValue> getValues();

    /**
     * Determine the next Sensor Value.<br>
     *
     * @return {@link SensorValue}; can be null
     */
    SensorValue measure();
}
