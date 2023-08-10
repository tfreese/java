package de.freese.sonstiges.dnd.basic;

/*
 * LabelDnD.java is a 1.4 example that requires no other files.
 */

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 * @author Thomas Freese
 */
public final class LabelDnD extends JPanel {
    @Serial
    private static final long serialVersionUID = -8569374356503714387L;

    /**
     * @author Thomas Freese
     */
    private static final class DragMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(final MouseEvent e) {
            JComponent c = (JComponent) e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, e, TransferHandler.COPY);
        }
    }

    public static void main(final String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(LabelDnD::createAndShowGUI);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("LabelDnD");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        JComponent newContentPane = new LabelDnD();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates a new {@link LabelDnD} object.
     */
    private LabelDnD() {
        super(new GridLayout(2, 1));

        JTextField textField = new JTextField(40);
        textField.setDragEnabled(true);

        JPanel panelTextField = new JPanel(new GridLayout(1, 1));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("JTextField: drag and drop is enabled");
        panelTextField.add(textField);
        panelTextField.setBorder(titledBorder);

        JLabel label = new JLabel("I'm a Label!", SwingConstants.LEADING);
        label.setTransferHandler(new TransferHandler("text"));

        MouseListener listener = new DragMouseAdapter();
        label.addMouseListener(listener);

        JPanel panelLabel = new JPanel(new GridLayout(1, 1));
        titledBorder = BorderFactory.createTitledBorder("JLabel: drag from or drop to this label");
        panelLabel.add(label);
        panelLabel.setBorder(titledBorder);

        add(panelTextField);
        add(panelLabel);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
}
