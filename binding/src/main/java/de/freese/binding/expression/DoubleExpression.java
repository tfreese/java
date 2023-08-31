// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.DoubleBinding;
import de.freese.binding.value.ObservableDoubleValue;
import de.freese.binding.value.ObservableNumberValue;

/**
 * @author Thomas Freese
 */
public interface DoubleExpression extends NumberExpression<Double>, ObservableDoubleValue {

    @Override
    default DoubleBinding add(final double value) {
        return (DoubleBinding) NumberExpression.super.add(value);
    }

    @Override
    default DoubleBinding add(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.add(other);
    }

    @Override
    default DoubleBinding divide(final double value) {
        return (DoubleBinding) NumberExpression.super.divide(value);
    }

    @Override
    default DoubleBinding divide(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.divide(other);
    }

    @Override
    default DoubleBinding multiply(final double value) {
        return (DoubleBinding) NumberExpression.super.multiply(value);
    }

    @Override
    default DoubleBinding multiply(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.multiply(other);
    }

    @Override
    default DoubleBinding subtract(final double value) {
        return (DoubleBinding) NumberExpression.super.subtract(value);
    }

    @Override
    default DoubleBinding subtract(final ObservableNumberValue<? extends Number> other) {
        return (DoubleBinding) NumberExpression.super.subtract(other);
    }
}
