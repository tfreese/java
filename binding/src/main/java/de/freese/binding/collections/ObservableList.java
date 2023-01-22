// Created: 08.08.2018
package de.freese.binding.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.event.ListDataListener;

/**
 * @author Thomas Freese
 */
public interface ObservableList<T> extends List<T>
{

    void addListener(ListDataListener listener);

    default FilteredObservableList<T> filtered(final Predicate<T> predicate)
    {
        return new FilteredObservableList<>(this, predicate);
    }

    boolean isListenerEnabled();

    /**
     * @param from int, inclusive
     * @param to int, exclusive
     */
    void remove(int from, int to);

    void removeListener(ListDataListener listener);

    default boolean setAll(final Collection<? extends T> col)
    {
        clear();

        return addAll(col);
    }

    void setListenerEnabled(boolean listenerEnabled);

    default SortedObservableList<T> sorted(final Comparator<T> comparator)
    {
        return new SortedObservableList<>(this, comparator);
    }
}
