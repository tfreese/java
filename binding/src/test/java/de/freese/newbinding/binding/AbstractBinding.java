// Created: 11 Aug. 2025
package de.freese.newbinding.binding;

import java.util.Objects;

import de.freese.newbinding.property.AbstractObservableProperty;

/**
 * @author Thomas Freese
 */
public abstract class AbstractBinding<T> extends AbstractObservableProperty<T> implements Binding<T> {
    private T value;

    protected AbstractBinding(final String name) {
        super(name);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void update() {
        final T old = value;
        value = computeValue();

        if (Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }

    /**
     * Update the new Value.
     */
    protected abstract T computeValue();

}
