// Created: 08.08.2018
package de.freese.binding.expression;

import de.freese.binding.collections.ObservableListValue;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface ListExpression<T> extends ObservableListValue<T>
{
    // /**
    // * @return {@link IntegerBinding}
    // */
    // public default IntegerBinding length()
    // {
    // return Bindings.length(this);
    // }
}
