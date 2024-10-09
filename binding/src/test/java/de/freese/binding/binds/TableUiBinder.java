// Created: 09 Okt. 2024
package de.freese.binding.binds;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JTable;

/**
 * @author Thomas Freese
 */
public class TableUiBinder<T> extends AbstractUiBinder<JTable, int[]> {
    public record MouseEventInfo(int clickCount, int row, int column) {
    }

    private final JTable jTable;
    private final List<Consumer<MouseEventInfo>> mouseListenerConsumers = new ArrayList<>();

    public TableUiBinder(final JTable jTable) {
        super();

        this.jTable = jTable;

        jTable.getSelectionModel().addListSelectionListener(event -> {
                    if (event.getValueIsAdjusting()) {
                        return;
                    }

                    final int[] selectedRows = jTable.getSelectedRows();

                    fireConsumers(selectedRows);
                }
        );
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                final Point point = event.getPoint();
                final int row = jTable.rowAtPoint(point);
                final int selectedRow = jTable.convertRowIndexToModel(row);
                final int column = jTable.columnAtPoint(point);
                final int selectedColumn = jTable.convertColumnIndexToModel(column);

                final MouseEventInfo info = new MouseEventInfo(event.getClickCount(), selectedRow, selectedColumn);
                mouseListenerConsumers.forEach(mlc -> mlc.accept(info));
            }
        });
    }

    public void addMouseListenerConsumer(final Consumer<MouseEventInfo> consumer) {
        mouseListenerConsumers.add(Objects.requireNonNull(consumer, "consumer required"));
    }

    @Override
    public JTable getComponent() {
        return jTable;
    }
}
