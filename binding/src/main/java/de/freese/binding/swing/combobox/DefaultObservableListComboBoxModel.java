// Created: 10.08.2018
package de.freese.binding.swing.combobox;

import java.io.Serial;

import de.freese.binding.collections.ObservableList;

/**
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class DefaultObservableListComboBoxModel<T> extends AbstractObservableListComboBoxModel<T>
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1454539310336157473L;

    /**
     * Erstellt ein neues {@link DefaultObservableListComboBoxModel} Object.
     *
     * @param list {@link ObservableList}
     */
    public DefaultObservableListComboBoxModel(final ObservableList<T> list)
    {
        super(list);
    }
}
