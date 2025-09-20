package de.freese.sonstiges.dnd.basic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Created on 31.08.2004
 */
public final class BasicDnD extends JPanel implements ActionListener {
    @Serial
    private static final long serialVersionUID = -1712718559600336190L;

    static void main() {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(BasicDnD::createAndShowGUI);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        // JFrame.setDefaultLookAndFeelDecorated(true);
        // Create and set up the window.
        final JFrame frame = new JFrame("BasicDnD");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        final JComponent newContentPane = new BasicDnD();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private final JColorChooser colorChooser;
    private final JList<String> list;
    private final JTable table;
    private final JTextArea textArea;
    private final JTextField textField;
    private final JCheckBox toggleDnD;
    private final JTree tree;

    private BasicDnD() {
        super(new BorderLayout());

        final JPanel leftPanel = createVerticalBoxPanel();
        final JPanel rightPanel = createVerticalBoxPanel();

        // Create a table model.
        final DefaultTableModel tm = new DefaultTableModel();
        tm.addColumn("Column 0");
        tm.addColumn("Column 1");
        tm.addColumn("Column 2");
        tm.addColumn("Column 3");
        tm.addRow(new String[]{"Table 00", "Table 01", "Table 02", "Table 03"});
        tm.addRow(new String[]{"Table 10", "Table 11", "Table 12", "Table 13"});
        tm.addRow(new String[]{"Table 20", "Table 21", "Table 22", "Table 23"});
        tm.addRow(new String[]{"Table 30", "Table 31", "Table 32", "Table 33"});

        // LEFT COLUMN
        // Use the table model to create a table.
        table = new JTable(tm);
        leftPanel.add(createPanelForComponent(table, "JTable"));

        // Create a dnd.color chooser.
        colorChooser = new JColorChooser();
        leftPanel.add(createPanelForComponent(colorChooser, "JColorChooser"));

        // RIGHT COLUMN
        // Create a TextField.
        textField = new JTextField(30);
        textField.setText("Favorite foods: Pizza, Moussaka, Pot roast");
        rightPanel.add(createPanelForComponent(textField, "JTextField"));

        // Create a scrolled text area.
        textArea = new JTextArea(5, 30);
        textArea.setText("Favorite shows:" + System.lineSeparator() + "Buffy, Alias, Angel");

        final JScrollPane scrollPane = new JScrollPane(textArea);
        rightPanel.add(createPanelForComponent(scrollPane, "JTextArea"));

        // Create a list model and a list.
        final DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Martha Washington");
        listModel.addElement("Abigail Adams");
        listModel.addElement("Martha Randolph");
        listModel.addElement("Dolley Madison");
        listModel.addElement("Elizabeth Monroe");
        listModel.addElement("Louisa Adams");
        listModel.addElement("Emily Donelson");
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        final JScrollPane listView = new JScrollPane(list);
        listView.setPreferredSize(new Dimension(300, 100));
        rightPanel.add(createPanelForComponent(listView, "JList"));

        // Create a tree.
        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Mia Familia");
        final DefaultMutableTreeNode sharon = new DefaultMutableTreeNode("Sharon");
        rootNode.add(sharon);

        final DefaultMutableTreeNode maya = new DefaultMutableTreeNode("Maya");
        sharon.add(maya);

        final DefaultMutableTreeNode anya = new DefaultMutableTreeNode("Anya");
        sharon.add(anya);
        sharon.add(new DefaultMutableTreeNode("Bongo"));
        maya.add(new DefaultMutableTreeNode("Muffin"));
        anya.add(new DefaultMutableTreeNode("Winky"));

        final DefaultTreeModel model = new DefaultTreeModel(rootNode);
        tree = new JTree(model);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        final JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(300, 100));
        rightPanel.add(createPanelForComponent(treeView, "JTree"));

        // Create the toggle button.
        toggleDnD = new JCheckBox("Turn on Drag and Drop");
        toggleDnD.setActionCommand("toggleDnD");
        toggleDnD.addActionListener(this);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER);
        add(toggleDnD, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if ("toggleDnD".equals(e.getActionCommand())) {
            final boolean toggle = toggleDnD.isSelected();
            textArea.setDragEnabled(toggle);
            textField.setDragEnabled(toggle);
            list.setDragEnabled(toggle);
            table.setDragEnabled(toggle);
            tree.setDragEnabled(toggle);
            colorChooser.setDragEnabled(toggle);
        }
    }

    public JPanel createPanelForComponent(final JComponent comp, final String title) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(comp, BorderLayout.CENTER);

        if (title != null) {
            panel.setBorder(BorderFactory.createTitledBorder(title));
        }

        return panel;
    }

    private JPanel createVerticalBoxPanel() {
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return p;
    }
}
