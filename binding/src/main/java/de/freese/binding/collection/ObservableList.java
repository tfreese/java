package de.freese.binding.collection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Thomas Freese
 * @since 09.08.26
 */
public final class ObservableList<T> extends AbstractList<T> {
    private final List<T> delegate;
    private final EventListenerList listenerList = new EventListenerList();

    private boolean listenerEnabled = true;

    public ObservableList(final List<T> delegate) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
    }

    @Override
    public void add(final int index, final T element) {
        delegate.add(index, element);

        fireIntervalAdded(index, index);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        boolean modified = false;
        int idx = index;

        for (final T element : c) {
            delegate.add(idx++, element);
            modified = true;
        }

        fireIntervalAdded(index, size() - 1);

        return modified;
    }

    public synchronized void addListener(final ListDataListener listener) {
        listenerList.add(ListDataListener.class, listener);
    }

    @Override
    public void clear() {
        if (size() == 0) {
            return;
        }

        final int endIndex = size() - 1;

        delegate.clear();

        fireIntervalRemoved(0, endIndex);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        if (!super.equals(o)) {
            return false;
        }

        final ObservableList<?> that = (ObservableList<?>) o;

        return listenerEnabled == that.listenerEnabled && Objects.equals(delegate, that.delegate) && Objects.equals(listenerList, that.listenerList);
    }

    @Override
    public T get(final int index) {
        return delegate.get(index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), delegate, listenerList, listenerEnabled);
    }

    public boolean isListenerEnabled() {
        return listenerEnabled;
    }

    @Override
    public T remove(final int index) {
        final T old = delegate.remove(index);

        fireIntervalRemoved(index, index);

        return old;
    }

    public synchronized void removeListener(final ListDataListener listener) {
        listenerList.remove(ListDataListener.class, listener);
    }

    @Override
    public T set(final int index, final T element) {
        final T old = delegate.set(index, element);

        fireContentsChanged(index, index);

        return old;
    }

    public void setListenerEnabled(final boolean listenerEnabled) {
        this.listenerEnabled = listenerEnabled;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void sort(final Comparator<? super T> c) {
        super.sort(c);

        fireContentsChanged(0, size() - 1);
    }

    private void fireContentsChanged(final int startIndex, final int endIndex) {
        if (!isListenerEnabled()) {
            return;
        }

        final int start = Math.min(startIndex, endIndex);
        final int end = Math.max(startIndex, endIndex);

        final ListDataListener[] listeners = listenerList.getListeners(ListDataListener.class);

        if (listeners.length == 0) {
            return;
        }

        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end);

        for (int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].contentsChanged(event);
        }
    }

    private void fireIntervalAdded(final int startIndex, final int endIndex) {
        if (!isListenerEnabled()) {
            return;
        }

        final int start = Math.min(startIndex, endIndex);
        final int end = Math.max(startIndex, endIndex);

        final ListDataListener[] listeners = listenerList.getListeners(ListDataListener.class);

        if (listeners.length == 0) {
            return;
        }

        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end);

        for (int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].intervalAdded(event);
        }
    }

    private void fireIntervalRemoved(final int startIndex, final int endIndex) {
        if (!isListenerEnabled()) {
            return;
        }

        final int start = Math.min(startIndex, endIndex);
        final int end = Math.max(startIndex, endIndex);

        final ListDataListener[] listeners = listenerList.getListeners(ListDataListener.class);

        if (listeners.length == 0) {
            return;
        }

        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start, end);

        for (int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].intervalRemoved(event);
        }
    }
}
