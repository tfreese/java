// Created: 31.07.2018
package de.freese.binding.value;

/**
 * Analog: javafx.beans.value.ChangeListener
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ChangeListener<T>
{
    void changed(ObservableValue<? extends T> observable, T oldValue, T newValue);
}
