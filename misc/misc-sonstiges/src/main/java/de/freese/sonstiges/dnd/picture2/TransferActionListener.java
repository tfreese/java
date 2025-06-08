package de.freese.sonstiges.dnd.picture2;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 * A class that tracks the focused component. This is necessary to delegate the menu cut/copy/paste commands to the right component. An instance of this class
 * is listening and when the user fires one of these commands, it calls the appropriate action on the currently focused component.
 *
 * @author Thomas Freese
 */
class TransferActionListener implements ActionListener, PropertyChangeListener {
    private JComponent focusOwner;

    TransferActionListener() {
        super();

        final KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", this);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        if (focusOwner == null) {
            return;
        }

        final String action = event.getActionCommand();
        final Action a = focusOwner.getActionMap().get(action);

        if (a != null) {
            a.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final Object o = event.getNewValue();

        if (o instanceof JComponent c) {
            focusOwner = c;
        }
        else {
            focusOwner = null;
        }
    }
}
