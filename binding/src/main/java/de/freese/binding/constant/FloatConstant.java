// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractFloatExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class FloatConstant extends AbstractFloatExpression
{
    /**
     * @param value float
     *
     * @return {@link FloatConstant}
     */
    public static FloatConstant valueOf(final float value)
    {
        return new FloatConstant(value);
    }

    /**
     *
     */
    private final float value;

    /**
     * Erstellt ein neues {@link FloatConstant} Object.
     *
     * @param value float
     */
    private FloatConstant(final float value)
    {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super Float> listener)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#fireValueChangedEvent(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void fireValueChangedEvent(final Float oldValue, final Float newValue)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Float getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super Float> listener)
    {
        // NO-OP
    }
}
