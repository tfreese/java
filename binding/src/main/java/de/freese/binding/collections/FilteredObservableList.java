// Created: 09.08.2018
package de.freese.binding.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Thomas Freese
 */
public class FilteredObservableList<T> extends DefaultObservableList<T> {

    private final List<T> filteredList = new ArrayList<>();

    private Predicate<T> predicate;

    public FilteredObservableList(final ObservableList<T> source) {
        this(source, null);
    }

    public FilteredObservableList(final ObservableList<T> source, final Predicate<T> predicate) {
        super(source);

        setPredicate(predicate);
        filteredList.addAll(source);

        source.addListener(new ListDataListener() {
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
                    FilteredObservableList.this.filteredList.remove(i);
                }
            }
        });
    }

    @Override
    public T get(final int index) {
        return filteredList.get(index);
    }

    public void setPredicate(final Predicate<T> predicate) {
        this.predicate = Objects.requireNonNull(predicate, "predicate required");
    }

    @Override
    public int size() {
        return filteredList.size();
    }

    protected void doFilter() {
        getLogger().debug("doFilter");

        filteredList.clear();

        for (T element : getSource()) {
            if (predicate.test(element)) {
                filteredList.add(element);
            }
        }
    }
}
