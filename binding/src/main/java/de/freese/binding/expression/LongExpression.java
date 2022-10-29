// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.LongBinding;
import de.freese.binding.value.ObservableLongValue;

/**
 * @author Thomas Freese
 */
public interface LongExpression extends NumberExpression<Long>, ObservableLongValue
{
    /**
     * @see de.freese.binding.expression.NumberExpression#add(long)
     */
    @Override
    default LongBinding add(final long value)
    {
        return (LongBinding) NumberExpression.super.add(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#divide(long)
     */
    @Override
    default LongBinding divide(final long value)
    {
        return (LongBinding) NumberExpression.super.divide(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#multiply(long)
     */
    @Override
    default LongBinding multiply(final long value)
    {
        return (LongBinding) NumberExpression.super.multiply(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#subtract(long)
     */
    @Override
    default LongBinding subtract(final long value)
    {
        return (LongBinding) NumberExpression.super.subtract(value);
    }
}
