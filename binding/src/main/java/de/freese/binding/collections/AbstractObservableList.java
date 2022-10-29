// Created: 08.08.2018
package de.freese.binding.collections;

import java.util.AbstractList;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public abstract class AbstractObservableList<T> extends AbstractList<T> implements ObservableList<T>
{
    /**
     *
     */
    private boolean listenerEnabled = true;
    /**
     *
     */
    private final EventListenerList listeners = new EventListenerList();
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @see java.util.AbstractList#add(int, java.lang.Object)
     */
    @Override
    public void add(final int index, final T element)
    {
        doAdd(index, element);

        fireIntervalAdded(index, index);
    }

    /**
     * @see de.freese.binding.collections.ObservableList#addListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void addListener(final ListDataListener listener)
    {
        getListeners().add(ListDataListener.class, listener);
    }

    /**
     * @see AbstractList#add(int, Object)
     */
    protected abstract void doAdd(int index, T element);

    /**
     * @see AbstractList#remove(int)
     */
    protected abstract T doRemove(int index);

    /**
     * @see AbstractList#set(int, Object)
     */
    protected abstract T doSet(int index, T element);

    /**
     *
     */
    protected void fireContentsChanged(final int startIndex, final int endIndex)
    {
        if (!isListenerEnabled())
        {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] l = getListeners().getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end);

        Runnable runnable = () ->
        {
            for (int i = l.length - 1; i >= 0; i--)
            {
                l[i].contentsChanged(event);
            }
        };

        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(runnable);
        }
        else
        {
            runnable.run();
        }
    }

    /**
     *
     */
    protected void fireIntervalAdded(final int startIndex, final int endIndex)
    {
        if (!isListenerEnabled())
        {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] l = getListeners().getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end);

        Runnable runnable = () ->
        {
            for (int i = l.length - 1; i >= 0; i--)
            {
                l[i].intervalAdded(event);
            }
        };

        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(runnable);
        }
        else
        {
            runnable.run();
        }
    }

    /**
     *
     */
    protected void fireIntervalRemoved(final int startIndex, final int endIndex)
    {
        if (!isListenerEnabled())
        {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] l = getListeners().getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start, end);

        Runnable runnable = () ->
        {
            for (int i = l.length - 1; i >= 0; i--)
            {
                l[i].intervalRemoved(event);
            }
        };

        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(runnable);
        }
        else
        {
            runnable.run();
        }
    }

    /**
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public abstract T get(int index);

    /**
     *
     */
    protected EventListenerList getListeners()
    {
        return this.listeners;
    }

    /**
     *
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.binding.collections.ObservableList#isListenerEnabled()
     */
    @Override
    public boolean isListenerEnabled()
    {
        return this.listenerEnabled;
    }

    /**
     * @see java.util.AbstractList#remove(int)
     */
    @Override
    public T remove(final int index)
    {
        T old = doRemove(index);

        fireIntervalRemoved(index, index);

        return old;
    }

    /**
     * @see de.freese.binding.collections.ObservableList#remove(int, int)
     */
    @Override
    public void remove(final int from, final int to)
    {
        removeRange(from, to);
    }

    /**
     * @see de.freese.binding.collections.ObservableList#removeListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void removeListener(final ListDataListener listener)
    {
        getListeners().remove(ListDataListener.class, listener);
    }

    /**
     * @see java.util.AbstractList#set(int, java.lang.Object)
     */
    @Override
    public T set(final int index, final T element)
    {
        T old = doSet(index, element);

        fireContentsChanged(index, index);

        return old;
    }

    /**
     * @see de.freese.binding.collections.ObservableList#setListenerEnabled(boolean)
     */
    @Override
    public void setListenerEnabled(final boolean listenerEnabled)
    {
        this.listenerEnabled = listenerEnabled;
    }

    /**
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public abstract int size();
}
