// Created: 31.07.2018
package de.freese.binding.property;

import java.util.Objects;

import de.freese.binding.expression.AbstractObjectExpression;

/**
 * Basis-Implementierung eines {@link Property} (Read/Write).
 *
 * @author Thomas Freese
 */
public abstract class AbstractObjectProperty<T> extends AbstractObjectExpression<T> implements Property<T>
{
    private T value;

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public T getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.value.WritableValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final T value)
    {
        T old = this.value;
        this.value = value;

        if (!Objects.equals(old, value))
        {
            fireValueChangedEvent(old, value);
        }
    }
}
