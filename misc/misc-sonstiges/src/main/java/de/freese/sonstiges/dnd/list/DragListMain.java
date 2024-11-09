package de.freese.sonstiges.dnd.list;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class DragListMain extends JPanel {
    @Serial
    private static final long serialVersionUID = -796699297895768926L;

    public static void main(final String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() throws ClassNotFoundException {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        final JFrame frame = new JFrame("DragListMain");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        final DragListMain demo = new DragListMain();
        frame.setContentPane(demo);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private DragListMain() throws ClassNotFoundException {
        super();

        final ArrayListTransferHandler arrayListHandler = new ArrayListTransferHandler();

        final JList<String> list1;
        final JList<String> list2;

        final DefaultListModel<String> list1Model = new DefaultListModel<>();
        list1Model.addElement("0 (list 1)");
        list1Model.addElement("1 (list 1)");
        list1Model.addElement("2 (list 1)");
        list1Model.addElement("3 (list 1)");
        list1Model.addElement("4 (list 1)");
        list1Model.addElement("5 (list 1)");
        list1Model.addElement("6 (list 1)");
        list1 = new JList<>(list1Model);
        list1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list1.setTransferHandler(arrayListHandler);
        list1.setDragEnabled(true);

        final JScrollPane list1View = new JScrollPane(list1);
        list1View.setPreferredSize(new Dimension(200, 100));

        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.add(list1View, BorderLayout.CENTER);
        panel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        final DefaultListModel<String> list2Model = new DefaultListModel<>();
        list2Model.addElement("0 (list 2)");
        list2Model.addElement("1 (list 2)");
        list2Model.addElement("2 (list 2)");
        list2Model.addElement("3 (list 2)");
        list2Model.addElement("4 (list 2)");
        list2Model.addElement("5 (list 2)");
        list2Model.addElement("6 (list 2)");
        list2 = new JList<>(list2Model);
        list2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list2.setTransferHandler(arrayListHandler);
        list2.setDragEnabled(true);

        final JScrollPane list2View = new JScrollPane(list2);
        list2View.setPreferredSize(new Dimension(200, 100));

        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.add(list2View, BorderLayout.CENTER);
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.LINE_START);
        add(panel2, BorderLayout.LINE_END);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}
