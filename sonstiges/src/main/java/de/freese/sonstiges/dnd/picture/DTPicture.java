package de.freese.sonstiges.dnd.picture;

import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serial;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

/**
 * @author Thomas Freese
 */
class DTPicture extends Picture implements MouseMotionListener {
    @Serial
    private static final long serialVersionUID = 7035716073636112614L;

    private static boolean installInputMapBindings = true;

    public static boolean hasInstallInputMapBindings() {
        // for completeness
        return installInputMapBindings;
    }

    // This method is necessary because DragPictureMain and
    // DragPicture2Main both use this class and DragPictureMain
    // needs to have the input map bindings installed for
    // cut/copy/paste. DragPicture2Main uses menu accelerators
    // and does not need to have the input map bindings installed.
    // Your program would use one approach or the other, but not
    // both. The default for installInputMapBindings is true.

    public static void setInstallInputMapBindings(final boolean flag) {
        installInputMapBindings = flag;
    }

    private MouseEvent firstMouseEvent;

    DTPicture(final Image image) {
        super(image);

        addMouseMotionListener(this);

        // Add the cut/copy/paste key bindings to the input map.
        // Note that this step is redundant if you are installing
        // menu accelerators that cause these actions to be invoked.
        // DragPictureMain does not use menu accelerators and, since
        // the default value of installInputMapBindings is true,
        // the bindings are installed. DragPicture2Main does use
        // menu accelerators and so calls setInstallInputMapBindings
        // with a value of false. Your program would do one or the
        // other, but not both.
        if (installInputMapBindings) {
            final InputMap imap = getInputMap();
            imap.put(KeyStroke.getKeyStroke("ctrl X"), TransferHandler.getCutAction().getValue(Action.NAME));
            imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler.getCopyAction().getValue(Action.NAME));
            imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler.getPasteAction().getValue(Action.NAME));
        }

        // Add the cut/copy/paste actions to the action map.
        // This step is necessary because the menu's action listener
        // looks for these actions to fire.
        final ActionMap map = getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
    }

    @Override
    public void mouseDragged(final MouseEvent event) {
        // Don't bother to drag if the component displays no image.
        if (getImage() == null) {
            return;
        }

        if (firstMouseEvent != null) {
            event.consume();

            // If they are holding down the control key, COPY rather than MOVE
            final int ctrlMask = InputEvent.CTRL_DOWN_MASK;
            final int action = ((event.getModifiersEx() & ctrlMask) == ctrlMask) ? TransferHandler.COPY : TransferHandler.MOVE;

            final int dx = Math.abs(event.getX() - firstMouseEvent.getX());
            final int dy = Math.abs(event.getY() - firstMouseEvent.getY());

            // Arbitrarily define a 5-pixel shift as the
            // official beginning of a drag.
            if (dx > 5 || dy > 5) {
                // This is a drag, not a click.
                final JComponent c = (JComponent) event.getSource();
                final TransferHandler handler = c.getTransferHandler();

                // Tell the transfer handler to initiate the drag.
                handler.exportAsDrag(c, firstMouseEvent, action);
                firstMouseEvent = null;
            }
        }
    }

    @Override
    public void mouseMoved(final MouseEvent event) {
        // Empty
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        // Don't bother to drag if there is no image.
        if (getImage() == null) {
            return;
        }

        firstMouseEvent = event;
        event.consume();
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
        firstMouseEvent = null;
    }

    @Override
    public void setImage(final Image image) {
        super.setImage(image);

        repaint();
    }
}
