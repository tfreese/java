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
 * @author Thomas Freese
 */
public abstract class AbstractObservableList<T> extends AbstractList<T> implements ObservableList<T> {

    private final EventListenerList listeners = new EventListenerList();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean listenerEnabled = true;

    @Override
    public void add(final int index, final T element) {
        doAdd(index, element);

        fireIntervalAdded(index, index);
    }

    @Override
    public void addListener(final ListDataListener listener) {
        getListeners().add(ListDataListener.class, listener);
    }

    @Override
    public abstract T get(int index);

    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public boolean isListenerEnabled() {
        return this.listenerEnabled;
    }

    @Override
    public T remove(final int index) {
        T old = doRemove(index);

        fireIntervalRemoved(index, index);

        return old;
    }

    @Override
    public void remove(final int from, final int to) {
        removeRange(from, to);
    }

    @Override
    public void removeListener(final ListDataListener listener) {
        getListeners().remove(ListDataListener.class, listener);
    }

    @Override
    public T set(final int index, final T element) {
        T old = doSet(index, element);

        fireContentsChanged(index, index);

        return old;
    }

    @Override
    public void setListenerEnabled(final boolean listenerEnabled) {
        this.listenerEnabled = listenerEnabled;
    }

    @Override
    public abstract int size();

    protected abstract void doAdd(int index, T element);

    protected abstract T doRemove(int index);

    protected abstract T doSet(int index, T element);

    protected void fireContentsChanged(final int startIndex, final int endIndex) {
        if (!isListenerEnabled()) {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] l = getListeners().getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end);

        Runnable runnable = () -> {
            for (int i = l.length - 1; i >= 0; i--) {
                l[i].contentsChanged(event);
            }
        };

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(runnable);
        }
        else {
            runnable.run();
        }
    }

    protected void fireIntervalAdded(final int startIndex, final int endIndex) {
        if (!isListenerEnabled()) {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] l = getListeners().getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end);

        Runnable runnable = () -> {
            for (int i = l.length - 1; i >= 0; i--) {
                l[i].intervalAdded(event);
            }
        };

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(runnable);
        }
        else {
            runnable.run();
        }
    }

    protected void fireIntervalRemoved(final int startIndex, final int endIndex) {
        if (!isListenerEnabled()) {
            return;
        }

        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);

        final ListDataListener[] l = getListeners().getListeners(ListDataListener.class);
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start, end);

        Runnable runnable = () -> {
            for (int i = l.length - 1; i >= 0; i--) {
                l[i].intervalRemoved(event);
            }
        };

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(runnable);
        }
        else {
            runnable.run();
        }
    }

    protected EventListenerList getListeners() {
        return this.listeners;
    }
}
