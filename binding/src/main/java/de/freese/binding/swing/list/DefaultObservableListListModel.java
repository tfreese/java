// Created: 10.08.2018
package de.freese.binding.swing.list;

import java.io.Serial;

import javax.swing.ListModel;

import de.freese.binding.collections.ObservableList;

/**
 * Default-Implementierung eines {@link ListModel} für die {@link ObservableList}.
 *
 * @author Thomas Freese
 */
public class DefaultObservableListListModel<T> extends AbstractObservableListListModel<T> {

    @Serial
    private static final long serialVersionUID = -578288830889934793L;

    public DefaultObservableListListModel(final ObservableList<T> list) {
        super(list);
    }
}
