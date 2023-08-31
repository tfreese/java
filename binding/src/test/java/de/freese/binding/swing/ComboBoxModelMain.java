// Created: 10.08.2018
package de.freese.binding.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import de.freese.binding.collections.DefaultObservableList;
import de.freese.binding.collections.ObservableList;
import de.freese.binding.swing.combobox.DefaultObservableListComboBoxModel;

/**
 * @author Thomas Freese
 */
public final class ComboBoxModelMain {

    public static void main(final String[] args) throws Exception {
        JFrame frame = new JFrame("Test-ComboBoxModel");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });

        ObservableList<Map<Integer, String>> list = new DefaultObservableList<>(new ArrayList<>());
        list = list.sorted((o1, o2) -> o2.get(0).compareTo(o1.get(1))); // Absteigend nach erster Spalte.
        list = list.filtered(map -> (Integer.parseInt(map.get(0).split("-")[0]) % 2) == 0); // Nur jede 2. Zeile

        JComboBox<Map<Integer, String>> comboBox = new JComboBox<>(new DefaultObservableListComboBoxModel<>(list));
        frame.add(comboBox);

        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Add Rows
        for (int i = 0; i < 7; i++) {
            TimeUnit.MILLISECONDS.sleep(2000);

            Map<Integer, String> row = new HashMap<>();
            row.put(0, i + "-0");
            row.put(1, i + "-1");
            row.put(2, i + "-2");
            row.put(3, i + "-3");

            list.add(row);
        }

        // Delete Rows
        TimeUnit.MILLISECONDS.sleep(2000);
        list.remove(0);
        TimeUnit.MILLISECONDS.sleep(2000);
        list.remove(2);

        TimeUnit.MILLISECONDS.sleep(2000);
        list.clear();
    }

    private ComboBoxModelMain() {
        super();
    }
}
