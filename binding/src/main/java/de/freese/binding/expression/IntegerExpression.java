// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.IntegerBinding;
import de.freese.binding.value.ObservableIntegerValue;

/**
 * @author Thomas Freese
 */
public interface IntegerExpression extends NumberExpression<Integer>, ObservableIntegerValue {

    @Override
    default IntegerBinding add(final int value) {
        return (IntegerBinding) NumberExpression.super.add(value);
    }

    @Override
    default IntegerBinding divide(final int value) {
        return (IntegerBinding) NumberExpression.super.divide(value);
    }

    @Override
    default IntegerBinding multiply(final int value) {
        return (IntegerBinding) NumberExpression.super.multiply(value);
    }

    @Override
    default IntegerBinding subtract(final int value) {
        return (IntegerBinding) NumberExpression.super.subtract(value);
    }
}
