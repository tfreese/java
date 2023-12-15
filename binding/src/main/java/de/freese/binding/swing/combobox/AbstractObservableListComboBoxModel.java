package de.freese.binding.swing.combobox;

import java.io.Serial;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;

import de.freese.binding.collections.ObservableList;
import de.freese.binding.swing.list.AbstractObservableListListModel;

/**
 * Basis ComboBoxModel, das intern eine {@link ObservableList} verwendet.
 *
 * @author Thomas Freese
 */
public abstract class AbstractObservableListComboBoxModel<T> extends AbstractObservableListListModel<T> implements ComboBoxModel<T> {
    @Serial
    private static final long serialVersionUID = -5837879226873538114L;

    /**
     * @author Thomas Freese
     */
    private final class ComboBoxEventListListener extends EventListListener {
        /**
         * Überschrieben, um sicherzustellen, das das selektierte Objekt in der ComboBox angepasst wird, wenn sich die Daten der {ObservableList} anpassen.
         */
        @Override
        public void contentsChanged(final ListDataEvent event) {
            AbstractObservableListComboBoxModel.this.selectedObject = null;

            super.contentsChanged(event);
        }
    }

    private transient Object selectedObject;

    protected AbstractObservableListComboBoxModel(final ObservableList<T> list) {
        super(list);
    }

    @Override
    public Object getSelectedItem() {
        return this.selectedObject;
    }

    @Override
    public void setSelectedItem(final Object anItem) {
        final int index = getList().indexOf(anItem);

        if (index != -1) {
            this.selectedObject = getList().get(index);
        }
        else {
            this.selectedObject = null;
        }

        fireContentsChanged(this, index, index);
    }

    @Override
    protected EventListListener createEventListener() {
        return new ComboBoxEventListListener();
    }

    /**
     * Überschrieben, da beim Entfernen von Objekten auch das selektierte Objekt der ComboBox angepasst werden muss.
     */
    @Override
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        if (this.selectedObject != null) {
            setSelectedItem(null);
        }

        super.fireIntervalRemoved(source, index0, index1);
    }
}
