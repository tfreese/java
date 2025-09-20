package de.freese.sonstiges.dnd.extended;

/*
 * StringTransferHandler.java is used by the 1.4 ExtendedDnDMain.java example.
 */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * @author Thomas Freese
 */
abstract class AbstractStringTransferHandler extends TransferHandler {
    @Serial
    private static final long serialVersionUID = -7174980141806424667L;

    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (DataFlavor.stringFlavor.equals(flavor)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(final JComponent c, final Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                final String str = (String) t.getTransferData(DataFlavor.stringFlavor);
                importString(c, str);

                return true;
            }
            catch (UnsupportedFlavorException | IOException _) {
                // Empty
            }
        }

        return false;
    }

    protected abstract void cleanup(JComponent c, boolean remove);

    @Override
    protected Transferable createTransferable(final JComponent c) {
        return new StringSelection(exportString(c));
    }

    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        cleanup(c, action == MOVE);
    }

    protected abstract String exportString(JComponent c);

    protected abstract void importString(JComponent c, String str);
}
