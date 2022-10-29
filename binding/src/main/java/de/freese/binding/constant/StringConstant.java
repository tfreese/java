// Created: 31.07.2018
package de.freese.binding.constant;

import de.freese.binding.expression.AbstractStringExpression;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public final class StringConstant extends AbstractStringExpression
{
    /**
     * @param value String
     *
     * @return {@link StringConstant}
     */
    public static StringConstant valueOf(final String value)
    {
        return new StringConstant(value);
    }

    /**
     *
     */
    private final String value;

    /**
     * Erstellt ein neues {@link StringConstant} Object.
     *
     * @param value {@link String}
     */
    private StringConstant(final String value)
    {
        super();

        this.value = value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super String> listener)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#fireValueChangedEvent(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void fireValueChangedEvent(final String oldValue, final String newValue)
    {
        // NO-OP
    }

    /**
     * @see de.freese.binding.value.ObservableValue#getValue()
     */
    @Override
    public String getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.binding.expression.AbstractExpression#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super String> listener)
    {
        // NO-OP
    }
}
