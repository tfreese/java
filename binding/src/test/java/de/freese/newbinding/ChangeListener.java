// Created: 31.07.2018
package de.freese.newbinding;

/**
 * Analog: javafx.beans.value.ChangeListener
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ChangeListener<T> {
    void changed(Property<? extends T> property, T oldValue, T newValue);
}
