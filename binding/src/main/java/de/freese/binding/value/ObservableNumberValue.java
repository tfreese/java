// Created: 01.08.2018
package de.freese.binding.value;

/**
 * @author Thomas Freese
 */
public interface ObservableNumberValue<T extends Number> extends ObservableValue<T> {
    default double doubleValue() {
        return getValue() == null ? 0D : getValue().doubleValue();
    }

    default float floatValue() {
        return getValue() == null ? 0F : getValue().floatValue();
    }

    default int intValue() {
        return getValue() == null ? 0 : getValue().intValue();
    }

    default long longValue() {
        return getValue() == null ? 0L : getValue().longValue();
    }
}
