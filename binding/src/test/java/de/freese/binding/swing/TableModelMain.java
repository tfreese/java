// Created: 10.08.2018
package de.freese.binding.swing;

import static org.awaitility.Awaitility.await;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.freese.binding.collections.DefaultObservableList;
import de.freese.binding.collections.ObservableList;
import de.freese.binding.swing.table.AbstractObservableListTableModel;

/**
 * @author Thomas Freese
 */
public final class TableModelMain {

    /**
     * @author Thomas Freese
     */
    private static class MyTableModel extends AbstractObservableListTableModel<Map<Integer, String>> {
        @Serial
        private static final long serialVersionUID = -4124180013372465407L;

        MyTableModel(final int columnCount, final ObservableList<Map<Integer, String>> list) {
            super(columnCount, list);
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final Map<Integer, String> map = getObjectAt(rowIndex);

            return map.get(columnIndex);
        }
    }

    public static void main(final String[] args) throws Exception {
        final JFrame frame = new JFrame("Test-TableModel");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });

        ObservableList<Map<Integer, String>> list = new DefaultObservableList<>(new ArrayList<>());
        list = list.sorted((o1, o2) -> o2.get(0).compareTo(o1.get(1))); // Absteigend nach erster Spalte.
        list = list.filtered(map -> (Integer.parseInt(map.get(0).split("-")[0]) % 2) == 0); // Nur jede 2. Zeile

        final JTable table = new JTable(new MyTableModel(4, list));
        frame.add(new JScrollPane(table));

        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Add Rows
        for (int i = 0; i < 7; i++) {
            await().pollDelay(Duration.ofMillis(2000L)).until(() -> true);

            final Map<Integer, String> row = new HashMap<>();
            row.put(0, i + "-0");
            row.put(1, i + "-1");
            row.put(2, i + "-2");
            row.put(3, i + "-3");

            list.add(row);
        }

        // Delete Rows
        await().pollDelay(Duration.ofMillis(2000L)).until(() -> true);
        list.remove(0);

        await().pollDelay(Duration.ofMillis(2000L)).until(() -> true);
        list.remove(2);

        await().pollDelay(Duration.ofMillis(2000L)).until(() -> true);
        list.clear();
    }

    private TableModelMain() {
        super();
    }
}
