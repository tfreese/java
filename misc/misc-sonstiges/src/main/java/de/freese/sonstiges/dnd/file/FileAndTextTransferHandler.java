package de.freese.sonstiges.dnd.file;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class FileAndTextTransferHandler extends TransferHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileAndTextTransferHandler.class);

    private static final String NEW_LINE = System.lineSeparator();
    @Serial
    private static final long serialVersionUID = 6906658392318378092L;

    private final DataFlavor fileFlavor;
    private final DataFlavor stringFlavor;

    private final transient TabbedPaneController tpc;
    private transient Position p0;
    private transient Position p1;
    private boolean shouldRemove;

    private JTextArea source;

    FileAndTextTransferHandler(final TabbedPaneController t) {
        super();

        this.tpc = t;
        this.fileFlavor = DataFlavor.javaFileListFlavor;
        this.stringFlavor = DataFlavor.stringFlavor;
    }

    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
        return hasFileFlavor(flavors) || hasStringFlavor(flavors);
    }

    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(final JComponent c, final Transferable t) {
        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }

        JTextArea textArea = null;

        // A real application would load the file in another
        // thread in order to not block the UI. This step
        // was omitted here to simplify the code.
        try {
            if (hasFileFlavor(t.getTransferDataFlavors())) {
                String str = null;
                final List<?> files = (List<?>) t.getTransferData(this.fileFlavor);

                for (Object file2 : files) {
                    final File file = (File) file2;

                    // Tell the TabbedPane controller to add
                    // a new tab with the name of this file
                    // on the tab. The text area that will
                    // display the contents of the file is returned.
                    textArea = this.tpc.addTab(file.toString());

                    try (BufferedReader in = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                        while ((str = in.readLine()) != null) {
                            textArea.append(str + NEW_LINE);
                        }
                    }
                    catch (IOException ex) {
                        LOGGER.error("importData: Unable to read from file %s".formatted(file), ex);
                    }
                }

                return true;
            }
            else if (hasStringFlavor(t.getTransferDataFlavors())) {
                textArea = (JTextArea) c;

                if (textArea.equals(this.source)
                        && textArea.getCaretPosition() >= this.p0.getOffset()
                        && textArea.getCaretPosition() <= this.p1.getOffset()) {
                    this.shouldRemove = false;

                    return true;
                }

                final String str = (String) t.getTransferData(this.stringFlavor);
                textArea.replaceSelection(str);

                return true;
            }
        }
        catch (UnsupportedFlavorException ex) {
            LOGGER.error("importData: unsupported data flavor", ex);
        }
        catch (IOException ex) {
            LOGGER.error("importData: I/O exception", ex);
        }

        return false;
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        this.source = (JTextArea) c;

        final int start = this.source.getSelectionStart();
        final int end = this.source.getSelectionEnd();
        final Document doc = this.source.getDocument();

        if (start == end) {
            return null;
        }

        try {
            this.p0 = doc.createPosition(start);
            this.p1 = doc.createPosition(end);
        }
        catch (BadLocationException ex) {
            LOGGER.error("Can't create position - unable to remove text from source.", ex);
        }

        this.shouldRemove = true;

        final String data = this.source.getSelectedText();

        return new StringSelection(data);
    }

    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (this.shouldRemove && action == MOVE) {
            if (this.p0 != null && this.p1 != null && this.p0.getOffset() != this.p1.getOffset()) {
                try {
                    final JTextComponent tc = (JTextComponent) c;
                    tc.getDocument().remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
                }
                catch (BadLocationException ex) {
                    LOGGER.error("Can't remove text from source.", ex);
                }
            }
        }

        this.source = null;
    }

    private boolean hasFileFlavor(final DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (this.fileFlavor.equals(flavor)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasStringFlavor(final DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (this.stringFlavor.equals(flavor)) {
                return true;
            }
        }

        return false;
    }
}
