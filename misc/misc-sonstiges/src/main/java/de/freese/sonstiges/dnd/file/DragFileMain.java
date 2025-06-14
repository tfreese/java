package de.freese.sonstiges.dnd.file;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class DragFileMain extends JPanel {
    @Serial
    private static final long serialVersionUID = 605490039316371414L;

    public static void main(final String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(DragFileMain::createAndShowGUI);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        final JFrame frame = new JFrame("DragFileMain");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the menu bar and content pane.
        final DragFileMain demo = new DragFileMain();
        demo.setOpaque(true); // content panes must be opaque
        frame.setContentPane(demo);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
        demo.setDefaultButton();
    }

    private final JButton clear;

    private final transient TabbedPaneController tpc;

    private DragFileMain() {
        super(new BorderLayout());

        final JFileChooser fc = new JFileChooser();

        fc.setMultiSelectionEnabled(true);
        fc.setDragEnabled(true);
        fc.setControlButtonsAreShown(false);

        final JPanel fcPanel = new JPanel(new BorderLayout());
        fcPanel.add(fc, BorderLayout.CENTER);

        clear = new JButton("Clear All");

        final JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.add(clear, BorderLayout.LINE_END);

        final JPanel upperPanel = new JPanel(new BorderLayout());
        upperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        upperPanel.add(fcPanel, BorderLayout.CENTER);
        upperPanel.add(buttonPanel, BorderLayout.PAGE_END);

        // The TabbedPaneController manages the panel that
        // contains the tabbed pane. When there are no files
        // the panel contains a plain text area. Then, as
        // files are dropped onto the area, the tabbed panel
        // replaces the file area.
        final JTabbedPane tabbedPane = new JTabbedPane();
        final JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tpc = new TabbedPaneController(tabbedPane, tabPanel);

        clear.addActionListener(event -> {
            if (event.getSource() == clear) {
                tpc.clearAll();
            }
        });

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, tabPanel);
        splitPane.setDividerLocation(400);
        splitPane.setPreferredSize(new Dimension(530, 650));
        add(splitPane, BorderLayout.CENTER);
    }

    public void setDefaultButton() {
        getRootPane().setDefaultButton(clear);
    }
}
