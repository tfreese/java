// Created: 08.08.2018
package de.freese.binding.binds;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.expression.ListExpression;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public interface ListBinding<T> extends Binding<ObservableList<T>>, ListExpression<T> {

}
