package de.freese.binding.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Thomas Freese
 * @since 09.08.2026
 */
public final class FilteredObservableList<T> extends ObservableList<T> {

    private final ObservableList<T> delegate;
    private final List<T> filteredList = new ArrayList<>();

    private Predicate<T> predicate;

    public FilteredObservableList(final ObservableList<T> delegate, final Predicate<T> predicate) {
        super(delegate);

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        setPredicate(predicate);

        this.delegate.addListener(new ListDataListener() {
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

                // One shift in List.
                if (lastRow >= firstRow) {
                    filteredList.subList(firstRow, lastRow + 1).clear();
                }

                // Every remove triggers one shift in List.
                // for (int i = lastRow; i >= firstRow; i--) {
                //     filteredList.remove(i);
                // }
            }
        });
    }

    @Override
    public boolean add(final T t) {
        return delegate.add(t);
    }

    @Override
    public void add(final int index, final T element) {
        delegate.add(index, element);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public T get(final int index) {
        return filteredList.get(index);
    }

    @Override
    public T remove(final int index) {
        return delegate.remove(index);
    }

    @Override
    public T set(final int index, final T element) {
        return delegate.set(index, element);
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
