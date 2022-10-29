// Created: 01.08.2018
package de.freese.binding.value;

/**
 * @author Thomas Freese
 */
public interface ObservableIntegerValue extends ObservableNumberValue<Integer>
{
    /**
     * Liefert den aktuellen Wert.
     *
     * @return int
     */
    int get();
}
