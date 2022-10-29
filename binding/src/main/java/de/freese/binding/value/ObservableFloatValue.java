// Created: 01.08.2018
package de.freese.binding.value;

/**
 * @author Thomas Freese
 */
public interface ObservableFloatValue extends ObservableNumberValue<Float>
{
    /**
     * Liefert den aktuellen Wert.
     *
     * @return float
     */
    float get();
}
