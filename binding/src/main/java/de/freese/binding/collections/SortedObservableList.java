// Created: 09.08.2018
package de.freese.binding.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Thomas Freese
 */
public class SortedObservableList<T> extends DefaultObservableList<T> {

    private final List<T> sortedList = new ArrayList<>();

    private Comparator<T> comparator;

    public SortedObservableList(final ObservableList<T> source) {
        this(source, null);
    }

    public SortedObservableList(final ObservableList<T> source, final Comparator<T> comparator) {
        super(source);

        setComparator(comparator);
        this.sortedList.addAll(source);

        source.addListener(new ListDataListener() {
            @Override
            public void contentsChanged(final ListDataEvent e) {
                doSort();
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                doSort();
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                // Empty
            }
        });
    }

    @Override
    public T get(final int index) {
        return this.sortedList.get(index);
    }

    public void setComparator(final Comparator<T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator required");
    }

    @Override
    protected void doAdd(final int index, final T element) {
        getLogger().debug("Index: {}; Element: {}", index, element);

        this.sortedList.add(index, element);

        super.doAdd(index, element);
    }

    @Override
    protected T doRemove(final int index) {
        getLogger().debug("Index: {}", index);

        this.sortedList.remove(index);

        return super.doRemove(index);
    }

    @Override
    protected T doSet(final int index, final T element) {
        getLogger().debug("Index: {}; Element: {}", index, element);

        this.sortedList.set(index, element);

        return super.doSet(index, element);
    }

    protected void doSort() {
        getLogger().debug("doSort");

        this.sortedList.sort(this.comparator);
    }
}
