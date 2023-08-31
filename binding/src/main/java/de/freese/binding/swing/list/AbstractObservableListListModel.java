package de.freese.binding.swing.list;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.freese.binding.collections.ObservableList;

/**
 * {@link ListModel} das intern eine {@link ObservableList} verwendet.
 *
 * @author Thomas Freese
 */
public abstract class AbstractObservableListListModel<T> implements ListModel<T>, Serializable {

    @Serial
    private static final long serialVersionUID = 1051092247879991757L;

    /**
     * Listener auf der {@link ObservableList}.
     *
     * @author Thomas Freese
     */
    protected class EventListListener implements ListDataListener {

        @Override
        public void contentsChanged(final ListDataEvent e) {
            fireContentsChanged(e.getSource(), e.getIndex0(), e.getIndex1());
        }

        @Override
        public void intervalAdded(final ListDataEvent e) {
            fireIntervalAdded(e.getSource(), e.getIndex0(), e.getIndex1());
        }

        @Override
        public void intervalRemoved(final ListDataEvent e) {
            fireIntervalRemoved(e.getSource(), e.getIndex0(), e.getIndex1());
        }
    }

    private final EventListenerList eventListenerList = new EventListenerList();

    private transient final ObservableList<T> list;

    protected AbstractObservableListListModel(final ObservableList<T> list) {
        super();

        this.list = Objects.requireNonNull(list, "list required");
        this.list.addListener(createEventListener());
    }

    @Override
    public synchronized void addListDataListener(final ListDataListener listener) {
        this.eventListenerList.add(ListDataListener.class, listener);
    }

    @Override
    public T getElementAt(final int index) {
        return getList().get(index);
    }

    public ObservableList<T> getList() {
        return this.list;
    }

    @Override
    public int getSize() {
        return getList().size();
    }

    @Override
    public synchronized void removeListDataListener(final ListDataListener listener) {
        this.eventListenerList.add(ListDataListener.class, listener);
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
     *
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
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
     *
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(final Object source, final int index0, final int index1) {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one or more elements are removed from the model. The new elements are
     * specified by a closed interval index0, index1, i.e. the range that includes both index0 and index1. Note that index0 need not be less than or equal to
     * index1.
     *
     * @param source the ListModel that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     *
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        Object[] listeners = this.eventListenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }

                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }
}
