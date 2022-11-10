// Created: 31.07.2018
package de.freese.binding.expression;

import de.freese.binding.Bindings;
import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.binds.IntegerBinding;
import de.freese.binding.binds.StringBinding;
import de.freese.binding.constant.StringConstant;
import de.freese.binding.value.ObservableStringValue;

/**
 * @author Thomas Freese
 */
public interface StringExpression extends ObservableStringValue
{
    default StringBinding concat(final ObservableStringValue other)
    {
        return Bindings.concat(this, other);
    }

    default StringBinding concat(final String value)
    {
        return concat(StringConstant.valueOf(value));
    }

    /**
     * Liefert einen leeren String "", wenn null.
     */
    default String getValueSafe()
    {
        final String value = getValue();

        return value == null ? "" : value;
    }

    default BooleanBinding isBlank()
    {
        return Bindings.isBlank(this);
    }

    default BooleanBinding isEmpty()
    {
        return Bindings.isEmpty(this);
    }

    default BooleanBinding isNotBlank()
    {
        return Bindings.isNotBlank(this);
    }

    default BooleanBinding isNotEmpty()
    {
        return Bindings.isNotEmpty(this);
    }

    default BooleanBinding isNotNull()
    {
        return Bindings.isNotNull(this);
    }

    default BooleanBinding isNull()
    {
        return Bindings.isNull(this);
    }

    default IntegerBinding length()
    {
        return Bindings.length(this);
    }
}
