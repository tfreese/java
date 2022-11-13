package de.freese.sonstiges.dnd.file;

import java.awt.BorderLayout;
import java.awt.Insets;

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
class TabbedPaneController
{
    private final JTabbedPane tabbedPane;

    private final JPanel tabbedPanel;
    private final FileAndTextTransferHandler transferHandler;
    private JTextArea emptyFileArea;
    private JPanel emptyFilePanel;
    private String fileSeparator;
    private boolean noFiles = true;

    TabbedPaneController(final JTabbedPane tb, final JPanel tp)
    {
        this.tabbedPane = tb;
        this.tabbedPanel = tp;
        this.transferHandler = new FileAndTextTransferHandler(this);
        this.fileSeparator = System.getProperty("file.separator");

        // The split method in the String class uses
        // regular expressions to define the text used for
        // the split. The forward slash "\" is a special
        // character and must be escaped. Some look and feels,
        // such as Microsoft Windows, use the forward slash to
        // delimit the path.
        if ("\\".equals(this.fileSeparator))
        {
            this.fileSeparator = "\\\\";
        }

        init();
    }

    public JTextArea addTab(final String filename)
    {
        if (this.noFiles)
        {
            this.tabbedPanel.remove(this.emptyFilePanel);
            this.tabbedPanel.add(this.tabbedPane, BorderLayout.CENTER);
            this.noFiles = false;
        }

        String[] str = filename.split(this.fileSeparator);

        return makeTextPanel(str[str.length - 1], filename);
    }

    public void clearAll()
    {
        if (!this.noFiles)
        {
            this.tabbedPane.removeAll();
            this.tabbedPanel.remove(this.tabbedPane);
        }

        init();
    }

    protected JTextArea makeTextPanel(final String name, final String toolTip)
    {
        JTextArea fileArea = new JTextArea(20, 15);
        fileArea.setDragEnabled(true);
        fileArea.setTransferHandler(this.transferHandler);
        fileArea.setMargin(new Insets(5, 5, 5, 5));

        JScrollPane fileScrollPane = new JScrollPane(fileArea);
        this.tabbedPane.addTab(name, null, fileScrollPane, toolTip);
        this.tabbedPane.setSelectedComponent(fileScrollPane);

        return fileArea;
    }

    private void init()
    {
        String defaultText = "Select one or more files from the file chooser and drop here...";
        this.noFiles = true;

        if (this.emptyFilePanel == null)
        {
            this.emptyFileArea = new JTextArea(20, 15);
            this.emptyFileArea.setEditable(false);
            this.emptyFileArea.setDragEnabled(true);
            this.emptyFileArea.setTransferHandler(this.transferHandler);
            this.emptyFileArea.setMargin(new Insets(5, 5, 5, 5));

            JScrollPane fileScrollPane = new JScrollPane(this.emptyFileArea);
            this.emptyFilePanel = new JPanel(new BorderLayout(), false);
            this.emptyFilePanel.add(fileScrollPane, BorderLayout.CENTER);
        }

        this.tabbedPanel.add(this.emptyFilePanel, BorderLayout.CENTER);
        this.tabbedPanel.repaint();
        this.emptyFileArea.setText(defaultText);
    }
}
