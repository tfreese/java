// Created: 08.08.2018
package de.freese.binding.collections;

import de.freese.binding.value.ObservableObjectValue;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface ObservableListValue<T> extends ObservableObjectValue<ObservableList<T>>, ObservableList<T>
{

}
