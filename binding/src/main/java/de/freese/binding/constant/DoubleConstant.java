// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractDoubleExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class DoubleConstant extends AbstractDoubleExpression
{
    /**
     * @param value double
     *
     * @return {@link DoubleConstant}
     */
    public static DoubleConstant valueOf(final double value)
    {
        return new DoubleConstant(value);
    }

    /**
     *
     */
    private final double value;

    /**
     * Erstellt ein neues {@link DoubleConstant} Object.
     *
     * @param value double
     */
    private DoubleConstant(final double value)
    {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super Double> listener)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#fireValueChangedEvent(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void fireValueChangedEvent(final Double oldValue, final Double newValue)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public Double getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super Double> listener)
    {
        // NO-OP
    }
}
