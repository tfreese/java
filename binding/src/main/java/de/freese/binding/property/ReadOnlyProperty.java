// Created: 31.07.2018
package de.freese.binding.property;

import de.freese.binding.value.ObservableValue;

/**
 * Analog: javafx.beans.property.ReadOnlyProperty
 *
 * @author Thomas Freese
 */
public interface ReadOnlyProperty<T> extends ObservableValue<T>
{
    /**
     * Liefert das Object, welchem dieses Property gehört oder null.
     */
    Object getBean();

    /**
     * Liefert den Namen der Property.
     */
    String getName();
}
