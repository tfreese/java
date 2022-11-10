// Created: 31.07.2018
package de.freese.binding.expression;

import de.freese.binding.Bindings;
import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.value.ObservableObjectValue;

/**
 * @author Thomas Freese
 */
public interface ObjectExpression<T> extends ObservableObjectValue<T>
{
    default BooleanBinding isNotNull()
    {
        return Bindings.isNotNull(this);
    }

    default BooleanBinding isNull()
    {
        return Bindings.isNull(this);
    }
}
