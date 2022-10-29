// Created: 31.07.2018
package de.freese.binding.value;

/**
 * Analog: javafx.beans.value.ObservableValue
 *
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface ObservableValue<T>
{
    /**
     * @param listener {@link ChangeListener}
     */
    void addListener(ChangeListener<? super T> listener);

    /**
     * Liefert den aktuellen Wert.
     *
     * @return Object
     */
    T getValue();

    /**
     * @param listener {@link ChangeListener}
     */
    void removeListener(ChangeListener<? super T> listener);
}
