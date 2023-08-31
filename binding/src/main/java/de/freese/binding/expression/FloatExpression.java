// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.binds.FloatBinding;
import de.freese.binding.value.ObservableFloatValue;

/**
 * @author Thomas Freese
 */
public interface FloatExpression extends NumberExpression<Float>, ObservableFloatValue {

    @Override
    default FloatBinding add(final float value) {
        return (FloatBinding) NumberExpression.super.add(value);
    }

    @Override
    default FloatBinding divide(final float value) {
        return (FloatBinding) NumberExpression.super.divide(value);
    }

    @Override
    default FloatBinding multiply(final float value) {
        return (FloatBinding) NumberExpression.super.multiply(value);
    }

    @Override
    default FloatBinding subtract(final float value) {
        return (FloatBinding) NumberExpression.super.subtract(value);
    }
}
