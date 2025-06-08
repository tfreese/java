// Created: 31.07.2018
package de.freese.binding.expression;

import java.util.ArrayList;
import java.util.List;

import de.freese.binding.value.ChangeListener;
import de.freese.binding.value.ObservableValue;

/**
 * Expression = ReadOnly-Property
 *
 * @author Thomas Freese
 */
public abstract class AbstractExpression<T> implements ObservableValue<T> {

    private final List<ChangeListener<? super T>> listeners = new ArrayList<>(4);

    @Override
    public void addListener(final ChangeListener<? super T> listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
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
