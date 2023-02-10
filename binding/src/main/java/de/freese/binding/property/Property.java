// Created: 31.07.2018
package de.freese.binding.property;

import de.freese.binding.value.WritableValue;

/**
 * Analog: javafx.beans.property.Property
 *
 * @author Thomas Freese
 */
public interface Property<T> extends ReadOnlyProperty<T>, WritableValue<T> {

}
