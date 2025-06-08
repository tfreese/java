package de.freese.sonstiges.dnd.color.textfield;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * An implementation of TransferHandler that adds support for dropping colors.<br>
 * Dropping a dnd color on a component having this TransferHandler changes the<br>
 * foreground or the background of the component to the dropped dnd color, according to the value of the changesForegroundColor property.
 */
class ColorTransferHandler extends TransferHandler {
    private static final String MIME_TYPE = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.awt.Color";

    @Serial
    private static final long serialVersionUID = -8161007208324025782L;

    private boolean changesForegroundColor = true;

    /**
     * The data type exported from JColorChooser.
     */
    private DataFlavor colorFlavor;

    ColorTransferHandler() {
        super();

        // Try to create a DataFlavor for dnd.color.
        try {
            colorFlavor = new DataFlavor(MIME_TYPE);
        }
        catch (ClassNotFoundException ex) {
            // Empty
        }
    }

    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
        return hasColorFlavor(flavors);
    }

    @Override
    public boolean importData(final JComponent c, final Transferable t) {
        if (hasColorFlavor(t.getTransferDataFlavors())) {
            try {
                final Color col = (Color) t.getTransferData(colorFlavor);

                if (hasChangesForegroundColor()) {
                    c.setForeground(col);
                }
                else {
                    c.setBackground(col);
                }

                return true;
            }
            catch (UnsupportedFlavorException ex) {
                System.out.println("importData: unsupported data flavor");
            }
            catch (IOException ex) {
                System.out.println("importData: I/O exception");
            }
        }

        return false;
    }

    protected boolean hasChangesForegroundColor() {
        return changesForegroundColor;
    }

    /**
     * Does the flavor list have a Color flavor?
     */
    protected boolean hasColorFlavor(final DataFlavor[] flavors) {
        if (colorFlavor == null) {
            return false;
        }

        for (DataFlavor flavor : flavors) {
            if (colorFlavor.equals(flavor)) {
                return true;
            }
        }

        return false;
    }

    protected void setChangesForegroundColor(final boolean flag) {
        changesForegroundColor = flag;
    }
}
