// Created: 31.07.2018
package de.freese.binding.expression;

import de.freese.binding.Bindings;
import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.constant.BooleanConstant;
import de.freese.binding.value.ObservableBooleanValue;

/**
 * @author Thomas Freese
 */
public interface BooleanExpression extends ObservableBooleanValue
{
    /**
     * @param value boolean
     *
     * @return {@link BooleanBinding}
     */
    default BooleanBinding and(final boolean value)
    {
        return Bindings.and(this, BooleanConstant.valueOf(value));
    }

    /**
     * @param other {@link ObservableBooleanValue}
     *
     * @return {@link BooleanBinding}
     */
    default BooleanBinding and(final ObservableBooleanValue other)
    {
        return Bindings.and(this, other);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isNotNull()
    {
        return Bindings.isNotNull(this);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isNull()
    {
        return Bindings.isNull(this);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding not()
    {
        return Bindings.not(this);
    }

    /**
     * @param value boolean
     *
     * @return {@link BooleanBinding}
     */
    default BooleanBinding or(final boolean value)
    {
        return Bindings.or(this, BooleanConstant.valueOf(value));
    }

    /**
     * @param other {@link ObservableBooleanValue}
     *
     * @return {@link BooleanBinding}
     */
    default BooleanBinding or(final ObservableBooleanValue other)
    {
        return Bindings.or(this, other);
    }
}
