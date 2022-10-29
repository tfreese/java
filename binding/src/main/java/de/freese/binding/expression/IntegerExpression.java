// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.IntegerBinding;
import de.freese.binding.value.ObservableIntegerValue;

/**
 * @author Thomas Freese
 */
public interface IntegerExpression extends NumberExpression<Integer>, ObservableIntegerValue
{
    /**
     * @see de.freese.binding.expression.NumberExpression#add(int)
     */
    @Override
    default IntegerBinding add(final int value)
    {
        return (IntegerBinding) NumberExpression.super.add(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#divide(int)
     */
    @Override
    default IntegerBinding divide(final int value)
    {
        return (IntegerBinding) NumberExpression.super.divide(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#multiply(int)
     */
    @Override
    default IntegerBinding multiply(final int value)
    {
        return (IntegerBinding) NumberExpression.super.multiply(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#subtract(int)
     */
    @Override
    default IntegerBinding subtract(final int value)
    {
        return (IntegerBinding) NumberExpression.super.subtract(value);
    }
}
