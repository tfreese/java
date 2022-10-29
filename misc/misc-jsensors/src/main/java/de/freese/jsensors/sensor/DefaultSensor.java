// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Default Implementation for a {@link Sensor}.
 *
 * @author Thomas Freese
 *
 * @param <T> Type of the object from which the value is extracted.
 */
public class DefaultSensor<T> extends AbstractSensor
{
    /**
     *
     */
    private final Function<T, String> function;
    /**
     *
     */
    private final WeakReference<T> ref;

    /**
     * Erstellt ein neues {@link DefaultSensor} Object.
     *
     * @param name String
     * @param obj Object
     * @param function {@link ToDoubleFunction}
     * @param keepLastNValues int
     * @param description String
     */
    public DefaultSensor(final String name, final T obj, final Function<T, String> function, final int keepLastNValues, final String description)
    {
        super(name, keepLastNValues, description);

        this.ref = new WeakReference<>(Objects.requireNonNull(obj, "obj required"));
        this.function = Objects.requireNonNull(function, "function required");
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#measure()
     */
    @Override
    public SensorValue measure()
    {
        T obj = this.ref.get();

        if (obj != null)
        {
            String functionValue = this.function.apply(obj);

            return addValue(functionValue);
        }

        return null;
    }
}
