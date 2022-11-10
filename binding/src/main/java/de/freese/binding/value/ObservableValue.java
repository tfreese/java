// Created: 31.07.2018
package de.freese.binding.value;

/**
 * Analog: javafx.beans.value.ObservableValue
 *
 * @author Thomas Freese
 */
public interface ObservableValue<T>
{
    void addListener(ChangeListener<? super T> listener);

    T getValue();

    void removeListener(ChangeListener<? super T> listener);
}
