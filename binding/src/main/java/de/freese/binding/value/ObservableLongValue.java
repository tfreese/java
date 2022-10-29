// Created: 01.08.2018
package de.freese.binding.value;

/**
 * @author Thomas Freese
 */
public interface ObservableLongValue extends ObservableNumberValue<Long>
{
    /**
     * Liefert den aktuellen Wert.
     *
     * @return long
     */
    long get();
}
