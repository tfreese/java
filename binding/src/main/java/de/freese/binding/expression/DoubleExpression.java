// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.DoubleBinding;
import de.freese.binding.value.ObservableDoubleValue;
import de.freese.binding.value.ObservableNumberValue;

/**
 * @author Thomas Freese
 */
public interface DoubleExpression extends NumberExpression<Double>, ObservableDoubleValue {
    /**
     * @see de.freese.binding.expression.NumberExpression#add(double)
     */
    @Override
    default DoubleBinding add(final double value) {
        return (DoubleBinding) NumberExpression.super.add(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#add(de.freese.binding.value.ObservableNumberValue)
     */
    @Override
    default DoubleBinding add(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.add(other);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#divide(double)
     */
    @Override
    default DoubleBinding divide(final double value) {
        return (DoubleBinding) NumberExpression.super.divide(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#divide(de.freese.binding.value.ObservableNumberValue)
     */
    @Override
    default DoubleBinding divide(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.divide(other);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#multiply(double)
     */
    @Override
    default DoubleBinding multiply(final double value) {
        return (DoubleBinding) NumberExpression.super.multiply(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#multiply(de.freese.binding.value.ObservableNumberValue)
     */
    @Override
    default DoubleBinding multiply(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.multiply(other);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#subtract(double)
     */
    @Override
    default DoubleBinding subtract(final double value) {
        return (DoubleBinding) NumberExpression.super.subtract(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#subtract(de.freese.binding.value.ObservableNumberValue)
     */
    @Override
    default DoubleBinding subtract(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.subtract(other);
    }
}
