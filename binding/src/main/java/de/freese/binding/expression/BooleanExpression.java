// Created: 31.07.2018
package de.freese.binding.expression;

import de.freese.binding.Bindings;
import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.constant.BooleanConstant;
import de.freese.binding.value.ObservableBooleanValue;

/**
 * @author Thomas Freese
 */
public interface BooleanExpression extends ObservableBooleanValue {

    default BooleanBinding and(final boolean value) {
        return Bindings.and(this, BooleanConstant.valueOf(value));
    }

    default BooleanBinding and(final ObservableBooleanValue other) {
        return Bindings.and(this, other);
    }

    default BooleanBinding isNotNull() {
        return Bindings.isNotNull(this);
    }

    default BooleanBinding isNull() {
        return Bindings.isNull(this);
    }

    default BooleanBinding not() {
        return Bindings.not(this);
    }

    default BooleanBinding or(final boolean value) {
        return Bindings.or(this, BooleanConstant.valueOf(value));
    }

    default BooleanBinding or(final ObservableBooleanValue other) {
        return Bindings.or(this, other);
    }
}
