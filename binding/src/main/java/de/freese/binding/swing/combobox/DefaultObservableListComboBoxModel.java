// Created: 10.08.2018
package de.freese.binding.swing.combobox;

import java.io.Serial;

import de.freese.binding.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public class DefaultObservableListComboBoxModel<T> extends AbstractObservableListComboBoxModel<T> {

    @Serial
    private static final long serialVersionUID = -1454539310336157473L;

    public DefaultObservableListComboBoxModel(final ObservableList<T> list) {
        super(list);
    }
}
