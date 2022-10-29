// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractObjectExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public final class ObjectConstant<T> extends AbstractObjectExpression<T>
{
    /**
     * @param value Object
     *
     * @return {@link ObjectConstant}
     */
    public static <T> ObjectConstant<T> valueOf(final T value)
    {
        return new ObjectConstant<>(value);
    }

    /**
     *
     */
    private final T value;

    /**
     * Erstellt ein neues {@link ObjectConstant} Object.
     *
     * @param value Object
     */
    private ObjectConstant(final T value)
    {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super T> listener)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#fireValueChangedEvent(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void fireValueChangedEvent(final T oldValue, final T newValue)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public T getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super T> listener)
    {
        // NO-OP
    }
}
