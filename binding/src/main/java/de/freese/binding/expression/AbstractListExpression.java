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
public abstract class AbstractListExpression<T> implements ObservableListValue<T>
{
    private final List<ChangeListener<? super ObservableList<T>>> listeners = new ArrayList<>(4);

    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(final int index, final T element)
    {
        getValue().add(index, element);
    }

    /**
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(final T e)
    {
        return getValue().add(e);
    }

    /**
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends T> c)
    {
        return getValue().addAll(c);
    }

    /**
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends T> c)
    {
        return getValue().addAll(index, c);
    }

    /**
     * @see de.freese.binding.value.ObservableValue#addListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void addListener(final ChangeListener<? super ObservableList<T>> listener)
    {
        if (!this.listeners.contains(listener))
        {
            this.listeners.add(listener);
        }
    }

    /**
     * @see de.freese.binding.collections.ObservableList#addListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void addListener(final ListDataListener listener)
    {
        getValue().addListener(listener);
    }

    /**
     * @see java.util.List#clear()
     */
    @Override
    public void clear()
    {
        getValue().clear();
    }

    /**
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(final Object o)
    {
        return getValue().contains(o);
    }

    /**
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return getValue().containsAll(c);
    }

    /**
     * @see java.util.List#get(int)
     */
    @Override
    public T get(final int index)
    {
        return getValue().get(index);
    }

    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(final Object o)
    {
        return getValue().indexOf(o);
    }

    /**
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return getValue().isEmpty();
    }

    /**
     * @see de.freese.binding.collections.ObservableList#isListenerEnabled()
     */
    @Override
    public boolean isListenerEnabled()
    {
        return getValue().isListenerEnabled();
    }

    /**
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<T> iterator()
    {
        return getValue().iterator();
    }

    /**
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(final Object o)
    {
        return getValue().lastIndexOf(o);
    }

    /**
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<T> listIterator()
    {
        return getValue().listIterator();
    }

    /**
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<T> listIterator(final int index)
    {
        return getValue().listIterator(index);
    }

    /**
     * @see java.util.List#remove(int)
     */
    @Override
    public T remove(final int index)
    {
        return getValue().remove(index);
    }

    /**
     * @see de.freese.binding.collections.ObservableList#remove(int, int)
     */
    @Override
    public void remove(final int from, final int to)
    {
        getValue().remove(from, to);
    }

    /**
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(final Object o)
    {
        return getValue().remove(o);
    }

    /**
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(final Collection<?> c)
    {
        return getValue().removeAll(c);
    }

    /**
     * @see de.freese.binding.value.ObservableValue#removeListener(de.freese.binding.value.ChangeListener)
     */
    @Override
    public void removeListener(final ChangeListener<? super ObservableList<T>> listener)
    {
        this.listeners.remove(listener);
    }

    /**
     * @see de.freese.binding.collections.ObservableList#removeListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void removeListener(final ListDataListener listener)
    {
        getValue().removeListener(listener);
    }

    /**
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(final Collection<?> c)
    {
        return getValue().retainAll(c);
    }

    /**
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public T set(final int index, final T element)
    {
        return getValue().set(index, element);
    }

    /**
     * @see de.freese.binding.collections.ObservableList#setAll(java.util.Collection)
     */
    @Override
    public boolean setAll(final Collection<? extends T> col)
    {
        return getValue().setAll(col);
    }

    /**
     * @see de.freese.binding.collections.ObservableList#setListenerEnabled(boolean)
     */
    @Override
    public void setListenerEnabled(final boolean listenerEnabled)
    {
        getValue().setListenerEnabled(listenerEnabled);
    }

    /**
     * @see java.util.List#size()
     */
    @Override
    public int size()
    {
        return getValue().size();
    }

    /**
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<T> subList(final int fromIndex, final int toIndex)
    {
        return getValue().subList(fromIndex, toIndex);
    }

    /**
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray()
    {
        return getValue().toArray();
    }

    /**
     * @see java.util.List#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(final T[] a)
    {
        return getValue().toArray(a);
    }

    protected void fireValueChangedEvent(final ObservableList<T> oldValue, final ObservableList<T> newValue)
    {
        for (ChangeListener<? super ObservableList<T>> changeListener : this.listeners)
        {
            changeListener.changed(this, oldValue, newValue);
        }
    }
}
