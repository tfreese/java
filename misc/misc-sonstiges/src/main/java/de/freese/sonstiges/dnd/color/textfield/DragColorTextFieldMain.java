package de.freese.sonstiges.dnd.color.textfield;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultEditorKit;

/**
 * Example code that shows a text component that both accepts dnd color (by changing its foreground) and also exports simple text.
 */
public final class DragColorTextFieldMain extends JPanel {
    @Serial
    private static final long serialVersionUID = 5524244374564444890L;

    public static void main(final String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(DragColorTextFieldMain::createAndShowGUI);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("DragColorTextFieldMain");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the menu bar and content pane.
        DragColorTextFieldMain demo = new DragColorTextFieldMain();
        frame.setJMenuBar(demo.createMenuBar());
        demo.setOpaque(true); // content panes must be opaque
        frame.setContentPane(demo);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private final ColorAndTextTransferHandler colorHandler;

    private JCheckBox toggleForeground;

    private DragColorTextFieldMain() {
        super(new BorderLayout());

        JTextField textField;

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JColorChooser chooser = new JColorChooser();
        chooser.setDragEnabled(true);
        add(chooser, BorderLayout.PAGE_START);

        // Create the dnd.color transfer handler.
        this.colorHandler = new ColorAndTextTransferHandler();

        // Create some text fields.
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        textField = new JTextField("I can accept dnd.color/text and drag text.");
        textField.setTransferHandler(this.colorHandler);
        textField.setDragEnabled(true);
        buttonPanel.add(textField);
        textField = new JTextField("Me too!");
        textField.setTransferHandler(this.colorHandler);
        textField.setDragEnabled(true);
        buttonPanel.add(textField);
        textField = new JTextField("Me three!");
        textField.setTransferHandler(this.colorHandler);
        textField.setDragEnabled(true);
        buttonPanel.add(textField);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public JMenuBar createMenuBar() {
        JMenuItem menuItem;
        JMenuBar menuBar = new JMenuBar();
        JMenu mainMenu = new JMenu("Edit");
        mainMenu.setMnemonic(KeyEvent.VK_E);

        menuItem = new JMenuItem(new DefaultEditorKit.CutAction());
        menuItem.setText("Cut");
        menuItem.setMnemonic(KeyEvent.VK_T);
        mainMenu.add(menuItem);
        menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItem.setText("Copy");
        menuItem.setMnemonic(KeyEvent.VK_C);
        mainMenu.add(menuItem);
        menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        menuItem.setText("Paste");
        menuItem.setMnemonic(KeyEvent.VK_P);
        mainMenu.add(menuItem);

        menuBar.add(mainMenu);

        return menuBar;
    }
}
