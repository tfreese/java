// Created: 08.08.2018
package de.freese.binding.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.ListDataListener;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.collections.ObservableListValue;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
public abstract class AbstractListExpression<T> implements ObservableListValue<T> {

    private final List<ChangeListener<? super ObservableList<T>>> listeners = new ArrayList<>(4);

    @Override
    public void add(final int index, final T element) {
        getValue().add(index, element);
    }

    @Override
    public boolean add(final T e) {
        return getValue().add(e);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        return getValue().addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return getValue().addAll(index, c);
    }

    @Override
    public void addListener(final ChangeListener<? super ObservableList<T>> listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void addListener(final ListDataListener listener) {
        getValue().addListener(listener);
    }

    @Override
    public void clear() {
        getValue().clear();
    }

    @Override
    public boolean contains(final Object o) {
        return getValue().contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return getValue().containsAll(c);
    }

    @Override
    public T get(final int index) {
        return getValue().get(index);
    }

    @Override
    public int indexOf(final Object o) {
        return getValue().indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Override
    public boolean isListenerEnabled() {
        return getValue().isListenerEnabled();
    }

    @Override
    public Iterator<T> iterator() {
        return getValue().iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
        return getValue().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getValue().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return getValue().listIterator(index);
    }

    @Override
    public T remove(final int index) {
        return getValue().remove(index);
    }

    @Override
    public void remove(final int from, final int to) {
        getValue().remove(from, to);
    }

    @Override
    public boolean remove(final Object o) {
        return getValue().remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return getValue().removeAll(c);
    }

    @Override
    public void removeListener(final ChangeListener<? super ObservableList<T>> listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeListener(final ListDataListener listener) {
        getValue().removeListener(listener);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return getValue().retainAll(c);
    }

    @Override
    public T set(final int index, final T element) {
        return getValue().set(index, element);
    }

    @Override
    public boolean setAll(final Collection<? extends T> col) {
        return getValue().setAll(col);
    }

    @Override
    public void setListenerEnabled(final boolean listenerEnabled) {
        getValue().setListenerEnabled(listenerEnabled);
    }

    @Override
    public int size() {
        return getValue().size();
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        return getValue().subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return getValue().toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return getValue().toArray(a);
    }

    protected void fireValueChangedEvent(final ObservableList<T> oldValue, final ObservableList<T> newValue) {
        for (ChangeListener<? super ObservableList<T>> changeListener : this.listeners) {
            changeListener.changed(this, oldValue, newValue);
        }
    }
}
