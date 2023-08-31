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

/**
 * An implementation of TransferHandler that adds support for the import of dnd color and the import and export of text.<br>
 * Dropping a dnd color on a component having this TransferHandler changes the foreground of the component to the imported dnd color.
 */
class ColorAndTextTransferHandler extends ColorTransferHandler {
    @Serial
    private static final long serialVersionUID = -2099117900708234471L;

    private transient final DataFlavor stringFlavor = DataFlavor.stringFlavor;

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
        JTextComponent tc = (JTextComponent) c;

        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }

        if (tc.equals(this.source) && (tc.getCaretPosition() >= this.p0.getOffset()) && (tc.getCaretPosition() <= this.p1.getOffset())) {
            this.shouldRemove = false;

            return true;
        }

        if (hasStringFlavor(t.getTransferDataFlavors())) {
            try {
                String str = (String) t.getTransferData(this.stringFlavor);
                tc.replaceSelection(str);

                return true;
            }
            catch (UnsupportedFlavorException ex) {
                System.out.println("importData: unsupported data flavor");
            }
            catch (IOException ex) {
                System.out.println("importData: I/O exception");
            }
        }

        // The ColorTransferHandler superclass handles dnd.color.
        return super.importData(c, t);
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        this.source = (JTextComponent) c;

        int start = this.source.getSelectionStart();
        int end = this.source.getSelectionEnd();
        Document doc = this.source.getDocument();

        if (start == end) {
            return null;
        }

        try {
            this.p0 = doc.createPosition(start);
            this.p1 = doc.createPosition(end);
        }
        catch (BadLocationException ex) {
            System.out.println("Can't create position - unable to remove text from source.");
        }

        this.shouldRemove = true;

        String data = this.source.getSelectedText();

        return new StringSelection(data);
    }

    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (this.shouldRemove && (action == MOVE)) {
            if ((this.p0 != null) && (this.p1 != null) && (this.p0.getOffset() != this.p1.getOffset())) {
                try {
                    JTextComponent tc = (JTextComponent) c;
                    tc.getDocument().remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
                }
                catch (BadLocationException ex) {
                    System.out.println("Can't remove text from source.");
                }
            }
        }

        this.source = null;
    }

    /**
     * Does the flavor list have a string flavor?
     */
    protected boolean hasStringFlavor(final DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (this.stringFlavor.equals(flavor)) {
                return true;
            }
        }

        return false;
    }
}
