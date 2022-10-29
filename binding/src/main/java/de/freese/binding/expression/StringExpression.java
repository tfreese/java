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
    /**
     * @param other {@link ObservableStringValue}
     *
     * @return {@link StringBinding}
     */
    default StringBinding concat(final ObservableStringValue other)
    {
        return Bindings.concat(this, other);
    }

    /**
     * @param value String
     *
     * @return {@link StringBinding}
     */
    default StringBinding concat(final String value)
    {
        return concat(StringConstant.valueOf(value));
    }

    /**
     * Liefert einen leeren String "", wenn null.
     *
     * @return String
     */
    default String getValueSafe()
    {
        final String value = getValue();

        return value == null ? "" : value;
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isBlank()
    {
        return Bindings.isBlank(this);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isEmpty()
    {
        return Bindings.isEmpty(this);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isNotBlank()
    {
        return Bindings.isNotBlank(this);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isNotEmpty()
    {
        return Bindings.isNotEmpty(this);
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
     * @return {@link IntegerBinding}
     */
    default IntegerBinding length()
    {
        return Bindings.length(this);
    }
}
