// Created: 31.07.2018
package de.freese.binding.property;

import java.util.Objects;

import de.freese.binding.expression.AbstractObjectExpression;

/**
 * Basis-Implementierung eines {@link Property} (Read/Write).
 *
 * @author Thomas Freese
 */
public abstract class AbstractObjectProperty<T> extends AbstractObjectExpression<T> implements Property<T> {

    private T value;

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(final T value) {
        final T old = this.value;
        this.value = value;

        if (!Objects.equals(old, value)) {
            fireValueChangedEvent(old, value);
        }
    }
}
