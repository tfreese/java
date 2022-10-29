// Created: 31.07.2018
package de.freese.binding.binds;

import de.freese.binding.expression.NumberExpression;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface NumberBinding<T extends Number> extends Binding<T>, NumberExpression<T>
{

}
