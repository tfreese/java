// Created: 08.08.2018
package de.freese.binding.collections;

import java.util.Arrays;
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
    default boolean addAll(final T... elements)
    {
        return addAll(Arrays.asList(elements));
    }

    void addListener(final ListDataListener listener);

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

    default boolean removeAll(final T... elements)
    {
        return removeAll(Arrays.asList(elements));
    }

    void removeListener(final ListDataListener listener);

    default boolean retainAll(final T... elements)
    {
        return retainAll(Arrays.asList(elements));
    }

    default boolean setAll(final Collection<? extends T> col)
    {
        clear();

        return addAll(col);
    }

    default boolean setAll(final T... elements)
    {
        return setAll(Arrays.asList(elements));
    }

    void setListenerEnabled(final boolean listenerEnabled);

    default SortedObservableList<T> sorted(final Comparator<T> comparator)
    {
        return new SortedObservableList<>(this, comparator);
    }
}
