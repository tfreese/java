// Created: 01.08.2018
package de.freese.binding.value;

/**
 * @author Thomas Freese
 */
public interface ObservableBooleanValue extends ObservableValue<Boolean>
{
    /**
     * Liefert den aktuellen Wert.
     *
     * @return boolean
     */
    boolean get();
}
