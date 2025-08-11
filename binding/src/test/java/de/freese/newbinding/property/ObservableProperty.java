// Created: 11 Aug. 2025
package de.freese.newbinding.property;

import de.freese.newbinding.ChangeListener;
import de.freese.newbinding.Property;

/**
 * @author Thomas Freese
 */
public interface ObservableProperty<T> extends Property<T> {
    void addListener(ChangeListener<? super T> listener);

    void removeListener(ChangeListener<? super T> listener);
}
