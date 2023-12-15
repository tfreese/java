// Created: 10.08.2018
package de.freese.binding.swing.table;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

/**
 * @author Thomas Freese
 */
public abstract class AbstractListTableModel<T> extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = 4681293552039847835L;

    private final int columnCount;

    private transient final List<String> columnNames;
    private transient final List<T> list;

    protected AbstractListTableModel(final int columnCount) {
        this(columnCount, new ArrayList<>());
    }

    protected AbstractListTableModel(final int columnCount, final List<T> list) {
        super();

        if (columnCount < 0) {
            throw new IllegalArgumentException("columnCount < 0: " + columnCount);
        }

        this.columnNames = null;
        this.columnCount = columnCount;
        this.list = Objects.requireNonNull(list, "list required");
    }

    protected AbstractListTableModel(final List<String> columnNames) {
        this(columnNames, new ArrayList<>());
    }

    protected AbstractListTableModel(final List<String> columnNames, final List<T> list) {
        super();

        this.columnNames = Objects.requireNonNull(columnNames, "columnNames required");
        this.columnCount = this.columnNames.size();

        this.list = Objects.requireNonNull(list, "list required");
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        if (getRowCount() != 0) {
            for (int row = 0; row < getRowCount(); row++) {
                final Object object = getValueAt(row, columnIndex);

                if (object != null) {
                    return object.getClass();
                }
            }
        }

        return super.getColumnClass(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return this.columnCount;
    }

    @Override
    public String getColumnName(final int column) {
        if ((getColumnNames() == null) || getColumnNames().isEmpty()) {
            return super.getColumnName(column);
        }

        return getColumnNames().get(column);
    }

    public final T getObjectAt(final int rowIndex) {
        // if ((rowIndex < 0) || (getList().size() <= rowIndex)) {
        // getLogger().warn("Falscher Index = " + rowIndex + "; ListSize = " + getList().size());
        //
        // // return null;
        // }

        return getList().get(rowIndex);
    }

    @Override
    public final int getRowCount() {
        return getList().size();
    }

    public final int getRowOf(final T object) {
        return getList().indexOf(object);
    }

    public void refresh() {
        fireTableDataChanged();

        // if (getRowCount() > 0) {
        // fireTableRowsUpdated(0, getRowCount() - 1);
        // }
    }

    protected List<String> getColumnNames() {
        return this.columnNames;
    }

    protected List<T> getList() {
        return this.list;
    }
}
