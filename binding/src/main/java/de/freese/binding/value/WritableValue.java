// Created: 31.07.2018
package de.freese.binding.value;

/**
 * Analog: javafx.beans.value.WritableValue
 *
 * @author Thomas Freese
 */
public interface WritableValue<T>
{
    T getValue();

    void setValue(T value);
}
