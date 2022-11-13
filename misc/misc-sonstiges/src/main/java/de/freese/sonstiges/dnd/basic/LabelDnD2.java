package de.freese.sonstiges.dnd.basic;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 * @author Thomas Freese
 */
public final class LabelDnD2 extends JPanel
{
    @Serial
    private static final long serialVersionUID = 8991761070190202012L;

    /**
     * @author Thomas Freese
     */
    private static class DragMouseAdapter extends MouseAdapter
    {
        /**
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(final MouseEvent e)
        {
            JComponent c = (JComponent) e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, e, TransferHandler.COPY);
        }
    }

    public static void main(final String[] args)
    {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(LabelDnD2::createAndShowGUI);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("LabelDnD2");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        JComponent newContentPane = new LabelDnD2();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private LabelDnD2()
    {
        super(new BorderLayout());

        JColorChooser chooser = new JColorChooser();
        chooser.setDragEnabled(true);

        JLabel label = new JLabel("I'm a Label and I accept dnd.color!", SwingConstants.LEADING);
        label.setTransferHandler(new TransferHandler("foreground"));

        // label.setTransferHandler(new TransferHandler("background"));
        MouseListener listener = new DragMouseAdapter();
        label.addMouseListener(listener);

        JPanel panel = new JPanel(new GridLayout(1, 1));
        TitledBorder t2 = BorderFactory.createTitledBorder("JLabel: drop dnd.color onto the label");
        panel.add(label);
        panel.setBorder(t2);

        add(chooser, BorderLayout.CENTER);
        add(panel, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
}
