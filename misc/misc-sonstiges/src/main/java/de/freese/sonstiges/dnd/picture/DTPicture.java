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
class DTPicture extends Picture implements MouseMotionListener
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7035716073636112614L;
    /**
     *
     */
    private static boolean installInputMapBindings = true;

    /**
     * @return boolean
     */
    public static boolean hasInstallInputMapBindings()
    { // for completeness

        return installInputMapBindings;
    }

    // This method is necessary because DragPictureDemo and
    // DragPictureDemo2 both use this class and DragPictureDemo
    // needs to have the input map bindings installed for
    // cut/copy/paste. DragPictureDemo2 uses menu accelerators
    // and does not need to have the input map bindings installed.
    // Your program would use one approach or the other, but not
    // both. The default for installInputMapBindings is true.

    /**
     * @param flag boolean
     */
    public static void setInstallInputMapBindings(final boolean flag)
    {
        installInputMapBindings = flag;
    }

    /**
     *
     */
    private MouseEvent firstMouseEvent;

    /**
     * Creates a new DTPicture object.
     *
     * @param image {@link Image}
     */
    public DTPicture(final Image image)
    {
        super(image);

        addMouseMotionListener(this);

        // Add the cut/copy/paste key bindings to the input map.
        // Note that this step is redundant if you are installing
        // menu accelerators that cause these actions to be invoked.
        // DragPictureDemo does not use menu accelerators and, since
        // the default value of installInputMapBindings is true,
        // the bindings are installed. DragPictureDemo2 does use
        // menu accelerators and so calls setInstallInputMapBindings
        // with a value of false. Your program would do one or the
        // other, but not both.
        if (installInputMapBindings)
        {
            InputMap imap = this.getInputMap();
            imap.put(KeyStroke.getKeyStroke("ctrl X"), TransferHandler.getCutAction().getValue(Action.NAME));
            imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler.getCopyAction().getValue(Action.NAME));
            imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler.getPasteAction().getValue(Action.NAME));
        }

        // Add the cut/copy/paste actions to the action map.
        // This step is necessary because the menu's action listener
        // looks for these actions to fire.
        ActionMap map = getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(final MouseEvent e)
    {
        // Don't bother to drag if the component displays no image.
        if (getImage() == null)
        {
            return;
        }

        if (this.firstMouseEvent != null)
        {
            e.consume();

            // If they are holding down the control key, COPY rather than MOVE
            int ctrlMask = InputEvent.CTRL_DOWN_MASK;
            int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ? TransferHandler.COPY : TransferHandler.MOVE;

            int dx = Math.abs(e.getX() - this.firstMouseEvent.getX());
            int dy = Math.abs(e.getY() - this.firstMouseEvent.getY());

            // Arbitrarily define a 5-pixel shift as the
            // official beginning of a drag.
            if ((dx > 5) || (dy > 5))
            {
                // This is a drag, not a click.
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();

                // Tell the transfer handler to initiate the drag.
                handler.exportAsDrag(c, this.firstMouseEvent, action);
                this.firstMouseEvent = null;
            }
        }
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(final MouseEvent e)
    {
        // Empty
    }

    /**
     * @see de.freese.sonstiges.dnd.picture.Picture#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {
        // Don't bother to drag if there is no image.
        if (getImage() == null)
        {
            return;
        }

        this.firstMouseEvent = e;
        e.consume();
    }

    /**
     * @see de.freese.sonstiges.dnd.picture.Picture#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
        this.firstMouseEvent = null;
    }

    /**
     * @param image {@link Image}
     */
    @Override
    public void setImage(final Image image)
    {
        super.setImage(image);

        this.repaint();
    }
}
