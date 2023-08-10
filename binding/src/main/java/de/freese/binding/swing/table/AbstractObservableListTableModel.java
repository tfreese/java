package de.freese.binding.swing.table;

import java.io.Serial;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.freese.binding.collections.ObservableList;

/**
 * TableModel das intern eine {@link ObservableList} verwendet.
 *
 * @author Thomas Freese
 */
public abstract class AbstractObservableListTableModel<T> extends AbstractListTableModel<T> {
    @Serial
    private static final long serialVersionUID = -863675813360039937L;

    /**
     * Listener auf der {@link ObservableList}.
     *
     * @author Thomas Freese
     */
    private final class EventListListener implements ListDataListener {
        @Override
        public void contentsChanged(final ListDataEvent e) {
            // int firstRow = e.getIndex0();
            // int lastRow = e.getIndex1();
            //
            // fireTableRowsUpdated(firstRow, lastRow);
            // fireTableRowsUpdated(0, Integer.MAX_VALUE);

            fireTableDataChanged();
        }

        @Override
        public void intervalAdded(final ListDataEvent e) {
            int firstRow = e.getIndex0();
            int lastRow = e.getIndex1();

            fireTableRowsInserted(firstRow, lastRow);
        }

        @Override
        public void intervalRemoved(final ListDataEvent e) {
            int firstRow = e.getIndex0();
            int lastRow = e.getIndex1();

            fireTableRowsDeleted(firstRow, lastRow);
        }
    }

    protected AbstractObservableListTableModel(final int columnCount, final ObservableList<T> list) {
        super(columnCount, list);

        list.addListener(new EventListListener());
    }

    protected AbstractObservableListTableModel(final List<String> columnNames, final ObservableList<T> list) {
        super(columnNames, list);

        list.addListener(new EventListListener());
    }

    @Override
    protected ObservableList<T> getList() {
        return (ObservableList<T>) super.getList();
    }
}
