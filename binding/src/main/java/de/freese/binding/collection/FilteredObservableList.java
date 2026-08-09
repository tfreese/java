package de.freese.binding.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Thomas Freese
 * @since 09.08.26
 */
public final class FilteredObservableList<T> extends AbstractList<T> {

    private final List<T> delegate;
    private final List<T> filteredList;

    private Predicate<T> predicate;

    public FilteredObservableList(final List<T> delegate, final Predicate<T> predicate) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.filteredList = new ArrayList<>(delegate);

        setPredicate(predicate);

        if (delegate instanceof final ObservableList<T> ol) {
            ol.addListener(new ListDataListener() {
                @Override
                public void contentsChanged(final ListDataEvent event) {
                    doFilter();
                }

                @Override
                public void intervalAdded(final ListDataEvent event) {
                    doFilter();
                }

                @Override
                public void intervalRemoved(final ListDataEvent event) {
                    final int firstRow = event.getIndex0();
                    final int lastRow = event.getIndex1();

                    for (int i = firstRow; i <= lastRow; i++) {
                        filteredList.remove(i);
                    }
                }
            });
        }
    }

    @Override
    public boolean add(final T t) {
        throw new UnsupportedOperationException("add(T) not supported, use delegate.add(T) instead");
    }

    @Override
    public void add(final int index, final T element) {
        throw new UnsupportedOperationException("add(int, T) not supported, use delegate.add(int, T) instead");
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        throw new UnsupportedOperationException("addAll(int, Collection) not supported, use delegate.addAll(int, Collection) instead");

    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() not supported, use delegate.clear() instead");
    }

    @Override
    public T get(final int index) {
        return filteredList.get(index);
    }

    @Override
    public T remove(final int index) {
        throw new UnsupportedOperationException("remove(int) not supported, use delegate.remove(int) instead");
    }

    @Override
    public T set(final int index, final T element) {
        throw new UnsupportedOperationException("set(int, T) not supported, use delegate.set(int, T) instead");
    }

    public void setPredicate(final Predicate<T> predicate) {
        this.predicate = Objects.requireNonNull(predicate, "predicate required");

        doFilter();
    }

    @Override
    public int size() {
        return filteredList.size();
    }

    private void doFilter() {
        filteredList.clear();

        for (final T element : delegate) {
            if (predicate.test(element)) {
                filteredList.add(element);
            }
        }
    }
}
