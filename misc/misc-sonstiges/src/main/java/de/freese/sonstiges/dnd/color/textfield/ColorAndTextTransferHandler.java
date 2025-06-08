package de.freese.sonstiges.dnd.color.textfield;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of TransferHandler that adds support for the import of dnd color and the import and export of text.<br>
 * Dropping a dnd color on a component having this TransferHandler changes the foreground of the component to the imported dnd color.
 */
class ColorAndTextTransferHandler extends ColorTransferHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ColorAndTextTransferHandler.class);

    private static final DataFlavor STRING_FLAVOR = DataFlavor.stringFlavor;

    @Serial
    private static final long serialVersionUID = -2099117900708234471L;

    private transient Position p0;
    private transient Position p1;

    private boolean shouldRemove;

    private JTextComponent source;

    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
        if (hasStringFlavor(flavors)) {
            return true;
        }

        return super.canImport(c, flavors);
    }

    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(final JComponent c, final Transferable t) {
        final JTextComponent tc = (JTextComponent) c;

        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }

        if (tc.equals(source) && tc.getCaretPosition() >= p0.getOffset() && tc.getCaretPosition() <= p1.getOffset()) {
            shouldRemove = false;

            return true;
        }

        if (hasStringFlavor(t.getTransferDataFlavors())) {
            try {
                final String str = (String) t.getTransferData(STRING_FLAVOR);
                tc.replaceSelection(str);

                return true;
            }
            catch (UnsupportedFlavorException ex) {
                LOGGER.error("importData: unsupported data flavor", ex);
            }
            catch (IOException ex) {
                LOGGER.error("importData: I/O exception", ex);
            }
        }

        // The ColorTransferHandler superclass handles dnd.color.
        return super.importData(c, t);
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        source = (JTextComponent) c;

        final int start = source.getSelectionStart();
        final int end = source.getSelectionEnd();
        final Document doc = source.getDocument();

        if (start == end) {
            return null;
        }

        try {
            p0 = doc.createPosition(start);
            p1 = doc.createPosition(end);
        }
        catch (BadLocationException ex) {
            LOGGER.error("Can't create position - unable to remove text from source.", ex);
        }

        shouldRemove = true;

        final String data = source.getSelectedText();

        return new StringSelection(data);
    }

    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (shouldRemove && action == MOVE) {
            if (p0 != null && p1 != null && p0.getOffset() != p1.getOffset()) {
                try {
                    final JTextComponent tc = (JTextComponent) c;
                    tc.getDocument().remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
                }
                catch (BadLocationException ex) {
                    LOGGER.error("Can't remove text from source.", ex);
                }
            }
        }

        source = null;
    }

    /**
     * Does the flavor list have a string flavor?
     */
    protected boolean hasStringFlavor(final DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (STRING_FLAVOR.equals(flavor)) {
                return true;
            }
        }

        return false;
    }
}
