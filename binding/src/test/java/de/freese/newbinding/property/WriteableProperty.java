// Created: 11 Aug. 2025
package de.freese.newbinding.property;

import de.freese.newbinding.Property;

/**
 * @author Thomas Freese
 */
public interface WriteableProperty<T> extends Property<T> {
    void setValue(T value);
}
