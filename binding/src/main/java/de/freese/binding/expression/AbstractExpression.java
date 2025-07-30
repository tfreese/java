// Created: 31.07.2018
package de.freese.binding.expression;

import java.util.LinkedHashSet;
import java.util.Set;

import de.freese.binding.value.ChangeListener;
import de.freese.binding.value.ObservableValue;

/**
 * Expression = ReadOnly-Property
 *
 * @author Thomas Freese
 */
public abstract class AbstractExpression<T> implements ObservableValue<T> {

    private final Set<ChangeListener<? super T>> listeners = LinkedHashSet.newLinkedHashSet(6);

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
