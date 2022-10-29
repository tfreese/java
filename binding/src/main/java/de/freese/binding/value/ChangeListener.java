// Created: 31.07.2018
package de.freese.binding.value;

/**
 * Analog: javafx.beans.value.ChangeListener
 *
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
@FunctionalInterface
public interface ChangeListener<T>
{
    /**
     * @param observable {@link ObservableValue}
     * @param oldValue Object
     * @param newValue Object
     */
    void changed(ObservableValue<? extends T> observable, T oldValue, T newValue);
}
