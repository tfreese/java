// Created: 31.07.2018
package de.freese.binding.expression;

import de.freese.binding.Bindings;
import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.value.ObservableObjectValue;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface ObjectExpression<T> extends ObservableObjectValue<T>
{
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
}
