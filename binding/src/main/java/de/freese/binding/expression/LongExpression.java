// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.LongBinding;
import de.freese.binding.value.ObservableLongValue;

/**
 * @author Thomas Freese
 */
public interface LongExpression extends NumberExpression<Long>, ObservableLongValue {

    @Override
    default LongBinding add(final long value) {
        return (LongBinding) NumberExpression.super.add(value);
    }

    @Override
    default LongBinding divide(final long value) {
        return (LongBinding) NumberExpression.super.divide(value);
    }

    @Override
    default LongBinding multiply(final long value) {
        return (LongBinding) NumberExpression.super.multiply(value);
    }

    @Override
    default LongBinding subtract(final long value) {
        return (LongBinding) NumberExpression.super.subtract(value);
    }
}
