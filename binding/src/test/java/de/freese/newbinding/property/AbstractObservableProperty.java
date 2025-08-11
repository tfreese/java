// Created: 11 Aug. 2025
package de.freese.newbinding.property;

import java.util.LinkedHashSet;
import java.util.Set;

import de.freese.newbinding.ChangeListener;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObservableProperty<T> extends AbstractProperty<T> implements ObservableProperty<T> {
    private final Set<ChangeListener<? super T>> listeners = LinkedHashSet.newLinkedHashSet(6);

    protected AbstractObservableProperty(final String name) {
        super(name);
    }

    @Override
    public void addListener(final ChangeListener<? super T> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final ChangeListener<? super T> listener) {
        listeners.remove(listener);
    }

    protected void fireValueChangedEvent(final T oldValue, final T newValue) {
        for (ChangeListener<? super T> changeListener : listeners) {
            changeListener.changed(this, oldValue, newValue);
        }
    }
}
