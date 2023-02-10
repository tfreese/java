// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.FloatBinding;
import de.freese.binding.value.ObservableFloatValue;

/**
 * @author Thomas Freese
 */
public interface FloatExpression extends NumberExpression<Float>, ObservableFloatValue {
    /**
     * @see de.freese.binding.expression.NumberExpression#add(float)
     */
    @Override
    default FloatBinding add(final float value) {
        return (FloatBinding) NumberExpression.super.add(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#divide(float)
     */
    @Override
    default FloatBinding divide(final float value) {
        return (FloatBinding) NumberExpression.super.divide(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#multiply(float)
     */
    @Override
    default FloatBinding multiply(final float value) {
        return (FloatBinding) NumberExpression.super.multiply(value);
    }

    /**
     * @see de.freese.binding.expression.NumberExpression#subtract(float)
     */
    @Override
    default FloatBinding subtract(final float value) {
        return (FloatBinding) NumberExpression.super.subtract(value);
    }
}
