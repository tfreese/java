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
class TransferActionListener implements ActionListener, PropertyChangeListener
{
    private JComponent focusOwner;

    TransferActionListener()
    {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", this);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        if (this.focusOwner == null)
        {
            return;
        }

        String action = e.getActionCommand();
        Action a = this.focusOwner.getActionMap().get(action);

        if (a != null)
        {
            a.actionPerformed(new ActionEvent(this.focusOwner, ActionEvent.ACTION_PERFORMED, null));
        }
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent e)
    {
        Object o = e.getNewValue();

        if (o instanceof JComponent c)
        {
            this.focusOwner = c;
        }
        else
        {
            this.focusOwner = null;
        }
    }
}
