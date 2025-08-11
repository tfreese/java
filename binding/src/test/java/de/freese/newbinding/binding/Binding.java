// Created: 11 Aug. 2025
package de.freese.newbinding.binding;

import de.freese.newbinding.Property;
import de.freese.newbinding.property.ObservableProperty;

/**
 * @author Thomas Freese
 */
public interface Binding<T> extends ObservableProperty<T> {
    /**
     * Update the Value by the registered {@link Property}<br>
     * and fire an event if the Value is different.
     */
    void update();
}
