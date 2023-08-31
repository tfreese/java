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
        this.filteredList.addAll(source);

        source.addListener(new ListDataListener() {
            @Override
            public void contentsChanged(final ListDataEvent e) {
                doFilter();
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                doFilter();
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                int firstRow = e.getIndex0();
                int lastRow = e.getIndex1();

                for (int i = firstRow; i <= lastRow; i++) {
                    FilteredObservableList.this.filteredList.remove(i);
                }
            }
        });
    }

    @Override
    public T get(final int index) {
        return this.filteredList.get(index);
    }

    public void setPredicate(final Predicate<T> predicate) {
        this.predicate = Objects.requireNonNull(predicate, "predicate required");
    }

    @Override
    public int size() {
        return this.filteredList.size();
    }

    protected void doFilter() {
        getLogger().debug("doFilter");

        this.filteredList.clear();

        for (T element : getSource()) {
            if (this.predicate.test(element)) {
                this.filteredList.add(element);
            }
        }
    }
}
