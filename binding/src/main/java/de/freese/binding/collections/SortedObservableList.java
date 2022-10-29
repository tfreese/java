// Created: 09.08.2018
package de.freese.binding.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class SortedObservableList<T> extends DefaultObservableList<T>
{
    /**
     *
     */
    private Comparator<T> comparator;
    /**
     *
     */
    private final List<T> sortedList = new ArrayList<>();

    /**
     * Erstellt ein neues {@link SortedObservableList} Object.
     *
     * @param source {@link ObservableList}
     */
    public SortedObservableList(final ObservableList<T> source)
    {
        this(source, null);
    }

    /**
     * Erstellt ein neues {@link SortedObservableList} Object.
     *
     * @param source {@link ObservableList}
     * @param comparator {@link Comparator}
     */
    public SortedObservableList(final ObservableList<T> source, final Comparator<T> comparator)
    {
        super(source);

        setComparator(comparator);
        this.sortedList.addAll(source);

        source.addListener(new ListDataListener()
        {
            /**
             * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
             */
            @Override
            public void contentsChanged(final ListDataEvent e)
            {
                doSort();
            }

            /**
             * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
             */
            @Override
            public void intervalAdded(final ListDataEvent e)
            {
                doSort();
            }

            /**
             * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
             */
            @Override
            public void intervalRemoved(final ListDataEvent e)
            {
                // NO-OP
            }
        });
    }

    /**
     * @see de.freese.binding.collections.DefaultObservableList#doAdd(int, java.lang.Object)
     */
    @Override
    protected void doAdd(final int index, final T element)
    {
        getLogger().debug("Index: {}; Element: {}", index, element);

        this.sortedList.add(index, element);

        super.doAdd(index, element);
    }

    /**
     * @see de.freese.binding.collections.DefaultObservableList#doRemove(int)
     */
    @Override
    protected T doRemove(final int index)
    {
        getLogger().debug("Index: {}", index);

        this.sortedList.remove(index);

        return super.doRemove(index);
    }

    /**
     * @see de.freese.binding.collections.DefaultObservableList#doSet(int, java.lang.Object)
     */
    @Override
    protected T doSet(final int index, final T element)
    {
        getLogger().debug("Index: {}; Element: {}", index, element);

        this.sortedList.set(index, element);

        return super.doSet(index, element);
    }

    /**
     *
     */
    protected void doSort()
    {
        getLogger().debug("doSort");

        this.sortedList.sort(this.comparator);
    }

    /**
     * @see de.freese.binding.collections.DefaultObservableList#get(int)
     */
    @Override
    public T get(final int index)
    {
        return this.sortedList.get(index);
    }

    /**
     * @param comparator {@link Comparator}
     */
    public void setComparator(final Comparator<T> comparator)
    {
        this.comparator = Objects.requireNonNull(comparator, "comparator required");
    }
}
