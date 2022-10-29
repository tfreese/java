// Created: 08.08.2018
package de.freese.binding.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.event.ListDataListener;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
@SuppressWarnings("unchecked")
public interface ObservableList<T> extends List<T>
{
    /**
     * Hinzufügen der Elemente.
     *
     * @param elements Object[]
     *
     * @return boolean
     *
     * @see List#add(Object)
     */
    default boolean addAll(final T... elements)
    {
        return addAll(Arrays.asList(elements));
    }

    /**
     * Hinzufügen eines {@link ListDataListener}s.
     *
     * @param listener {@link ListDataListener}
     */
    void addListener(final ListDataListener listener);

    /**
     * @param predicate {@link Predicate}
     *
     * @return {@link FilteredObservableList}
     */
    default FilteredObservableList<T> filtered(final Predicate<T> predicate)
    {
        return new FilteredObservableList<>(this, predicate);
    }

    /**
     * Liefert true, wenn die Events gefeuert werden.
     *
     * @return boolean
     */
    boolean isListenerEnabled();

    /**
     * @param from int, inclusive
     * @param to int, exclusive
     */
    void remove(int from, int to);

    /**
     * @param elements Object[]
     *
     * @return boolean
     *
     * @see List#remove(Object)
     */
    default boolean removeAll(final T... elements)
    {
        return removeAll(Arrays.asList(elements));
    }

    /**
     * Entfernen eines {@link ListDataListener}s.
     *
     * @param listener {@link ListDataListener}
     */
    void removeListener(final ListDataListener listener);

    /**
     * @param elements Object[]
     *
     * @return boolean
     *
     * @see List#retainAll(Collection)
     */
    default boolean retainAll(final T... elements)
    {
        return retainAll(Arrays.asList(elements));
    }

    /**
     * Leeren der Liste und hinzufügen der Elemente.
     *
     * @param col {@link Collection}
     *
     * @return boolean
     *
     * @see List#set(int, Object)
     */
    default boolean setAll(final Collection<? extends T> col)
    {
        clear();

        return addAll(col);
    }

    /**
     * Leeren der Liste und hinzufügen der Elemente.
     *
     * @param elements Object[]
     *
     * @return boolean
     *
     * @see List#set(int, Object)
     */
    default boolean setAll(final T... elements)
    {
        return setAll(Arrays.asList(elements));
    }

    /**
     * True, wenn die Events gefeuert werden sollen.
     *
     * @param listenerEnabled boolean
     */
    void setListenerEnabled(final boolean listenerEnabled);

    /**
     * @param comparator {@link Comparator}
     *
     * @return {@link SortedObservableList}
     */
    default SortedObservableList<T> sorted(final Comparator<T> comparator)
    {
        return new SortedObservableList<>(this, comparator);
    }
}
