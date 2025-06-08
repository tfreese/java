package de.freese.sonstiges.dnd.file;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.nio.file.FileSystems;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * Class that manages area where the contents of files are displayed. When no files are present, there is a simple JTextArea instructing users to drop a file.
 * As soon as a file is dropped, a JTabbedPane is placed into the window and each file is displayed under its own tab. When all the files are removed, the
 * JTabbedPane is removed from the window and the simple JTextArea is again displayed.
 *
 * @author Thomas Freese
 */
class TabbedPaneController {
    private final JTabbedPane tabbedPane;
    private final JPanel tabbedPanel;
    private final FileAndTextTransferHandler transferHandler;

    private JTextArea emptyFileArea;
    private JPanel emptyFilePanel;
    private String fileSeparator;
    private boolean noFiles = true;

    TabbedPaneController(final JTabbedPane tb, final JPanel tp) {
        super();

        tabbedPane = tb;
        tabbedPanel = tp;
        transferHandler = new FileAndTextTransferHandler(this);
        // fileSeparator = System.getProperty("file.separator");
        fileSeparator = FileSystems.getDefault().getSeparator();

        // The split method in the String class uses
        // regular expressions to define the text used for
        // the split. The forward slash "\" is a special
        // character and must be escaped. Some look and feels,
        // such as Microsoft Windows, use the forward slash to
        // delimit the path.
        if ("\\".equals(fileSeparator)) {
            fileSeparator = "\\\\";
        }

        init();
    }

    public JTextArea addTab(final String filename) {
        if (noFiles) {
            tabbedPanel.remove(emptyFilePanel);
            tabbedPanel.add(tabbedPane, BorderLayout.CENTER);
            noFiles = false;
        }

        final String[] str = filename.split(fileSeparator);

        return makeTextPanel(str[str.length - 1], filename);
    }

    public void clearAll() {
        if (!noFiles) {
            tabbedPane.removeAll();
            tabbedPanel.remove(tabbedPane);
        }

        init();
    }

    protected JTextArea makeTextPanel(final String name, final String toolTip) {
        final JTextArea fileArea = new JTextArea(20, 15);
        fileArea.setDragEnabled(true);
        fileArea.setTransferHandler(transferHandler);
        fileArea.setMargin(new Insets(5, 5, 5, 5));

        final JScrollPane fileScrollPane = new JScrollPane(fileArea);
        tabbedPane.addTab(name, null, fileScrollPane, toolTip);
        tabbedPane.setSelectedComponent(fileScrollPane);

        return fileArea;
    }

    private void init() {
        noFiles = true;

        if (emptyFilePanel == null) {
            emptyFileArea = new JTextArea(20, 15);
            emptyFileArea.setEditable(false);
            emptyFileArea.setDragEnabled(true);
            emptyFileArea.setTransferHandler(transferHandler);
            emptyFileArea.setMargin(new Insets(5, 5, 5, 5));

            final JScrollPane fileScrollPane = new JScrollPane(emptyFileArea);
            emptyFilePanel = new JPanel(new BorderLayout(), false);
            emptyFilePanel.add(fileScrollPane, BorderLayout.CENTER);
        }

        tabbedPanel.add(emptyFilePanel, BorderLayout.CENTER);
        tabbedPanel.repaint();
        emptyFileArea.setText("Select one or more files from the file chooser and drop here...");
    }
}
