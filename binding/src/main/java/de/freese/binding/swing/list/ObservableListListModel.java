package de.freese.binding.swing.list;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.freese.binding.collection.ObservableList;

/**
 * @author Thomas Freese
 * @since 09.08.26
 */
public class ObservableListListModel<T> implements ListModel<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1051092247879991757L;

    protected class EventListListener implements ListDataListener {
        @Override
        public void contentsChanged(final ListDataEvent event) {
            fireContentsChanged(event.getSource(), event.getIndex0(), event.getIndex1());
        }

        @Override
        public void intervalAdded(final ListDataEvent event) {
            fireIntervalAdded(event.getSource(), event.getIndex0(), event.getIndex1());
        }

        @Override
        public void intervalRemoved(final ListDataEvent event) {
            fireIntervalRemoved(event.getSource(), event.getIndex0(), event.getIndex1());
        }
    }

    private final EventListenerList listenerList = new EventListenerList();

    private final transient ObservableList<T> observableList;

    public ObservableListListModel(final ObservableList<T> observableList) {
        super();

        this.observableList = Objects.requireNonNull(observableList, "observableList required");
        this.observableList.addListener(createEventListener());
    }

    @Override
    public synchronized void addListDataListener(final ListDataListener listener) {
        listenerList.add(ListDataListener.class, listener);
    }

    @Override
    public T getElementAt(final int index) {
        return getList().get(index);
    }

    public ObservableList<T> getList() {
        return observableList;
    }

    @Override
    public int getSize() {
        return getList().size();
    }

    @Override
    public synchronized void removeListDataListener(final ListDataListener listener) {
        listenerList.remove(ListDataListener.class, listener);
    }

    protected EventListListener createEventListener() {
        return new EventListListener();
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements of the list change. The changed elements are specified
     * by the closed interval index0, index1 -- the endpoints are included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     */
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {
        final Object[] listeners = listenerList.getListenerList();

        if (listeners.length == 0) {
            return;
        }

        final ListDataEvent event = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                ((ListDataListener) listeners[i + 1]).contentsChanged(event);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements are added to the model. The new elements are specified
     * by a closed interval index0, index1 -- the endpoints are included. Note that index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     */
    protected void fireIntervalAdded(final Object source, final int index0, final int index1) {
        final Object[] listeners = listenerList.getListenerList();

        if (listeners.length == 0) {
            return;
        }

        final ListDataEvent event = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                ((ListDataListener) listeners[i + 1]).intervalAdded(event);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements are removed from the model. The new elements are
     * specified by a closed interval index0, index1, i.e., the range that includes both index0 and index1. Note that index0 need not be less than or equal to
     * index1.
     *
     * @param source the ListModel that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     */
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        final Object[] listeners = listenerList.getListenerList();

        if (listeners.length == 0) {
            return;
        }

        final ListDataEvent event = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                ((ListDataListener) listeners[i + 1]).intervalRemoved(event);
            }
        }
    }
}
