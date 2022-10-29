// Created: 01.08.2018
package de.freese.binding.value;

/**
 * @author Thomas Freese
 */
public interface ObservableDoubleValue extends ObservableNumberValue<Double>
{
    /**
     * Liefert den aktuellen Wert.
     *
     * @return double
     */
    double get();
}
