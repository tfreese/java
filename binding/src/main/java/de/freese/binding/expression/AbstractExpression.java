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

    /**
     * @see de.freese.binding.value.ObservableValue#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super T> listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    /**
     * @see de.freese.binding.value.ObservableValue#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super T> listener) {
        this.listeners.remove(listener);
    }

    protected void fireValueChangedEvent(final T oldValue, final T newValue) {
        for (ChangeListener<? super T> changeListener : this.listeners) {
            changeListener.changed(this, oldValue, newValue);
        }
    }
}
