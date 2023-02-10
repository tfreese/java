// Created: 02.09.2021
package de.freese.jsensors.sensor;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

/**
 * Default Implementation for a {@link Sensor}.
 *
 * @param <T> Type of the object from which the value is extracted.
 *
 * @author Thomas Freese
 */
public class DefaultSensor<T> extends AbstractSensor {
    private final WeakReference<T> ref;
    private final Function<T, String> valueFunction;

    public DefaultSensor(final String name, final T obj, final Function<T, String> valueFunction, final int keepLastNValues, final String description) {
        super(name, keepLastNValues, description);

        this.ref = new WeakReference<>(Objects.requireNonNull(obj, "obj required"));
        this.valueFunction = Objects.requireNonNull(valueFunction, "valueFunction required");
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#measure()
     */
    @Override
    public SensorValue measure() {
        T obj = this.ref.get();

        if (obj != null) {
            String functionValue = this.valueFunction.apply(obj);

            return addValue(functionValue);
        }

        return null;
    }
}
