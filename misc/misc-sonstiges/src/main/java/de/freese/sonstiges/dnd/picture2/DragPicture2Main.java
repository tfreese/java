package de.freese.sonstiges.dnd.picture2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.net.URL;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

/**
 * A version of DragPictureMain that creates an Edit menu with cut/copy/paste actions. This demo adds a class called TransferActionDemo that transfers the
 * cut/copy/paste menu action to the currently focused component.
 *
 * @author Thomas Freese
 */
public final class DragPicture2Main extends JPanel {
    private static final String ADELE = "Adele";
    private static final String ALEXI = "Alexi";
    private static final String ANYA = "Anya";
    private static final String COSMO = "Cosmo";
    private static final String LAINE = "Laine";
    private static final String MAYA = "Maya";

    @Serial
    private static final long serialVersionUID = -3292264415574380091L;

    public static void main(final String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(DragPicture2Main::createAndShowGUI);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        final JFrame frame = new JFrame("DragPicture2Main");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the menu bar and content pane.
        final DragPicture2Main demo = new DragPicture2Main();
        frame.setJMenuBar(demo.createMenuBar());
        demo.setOpaque(true); // content panes must be opaque
        frame.setContentPane(demo);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    private static ImageIcon createImageIcon(final String path, final String description) {
        final URL imageURL = DragPicture2Main.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);

            return null;
        }

        return new ImageIcon(imageURL, description);
    }

    private DragPicture2Main() {
        super(new BorderLayout());

        final PictureTransferHandler picHandler = new PictureTransferHandler();

        // Since we are using keyboard accelerators, we don't
        // need the component to install its own input map
        // bindings.
        DTPicture.setInstallInputMapBindings(false);

        final JPanel mugshots = new JPanel(new GridLayout(4, 3));
        final DTPicture pic1 = new DTPicture(createImageIcon(MAYA + ".jpg", MAYA).getImage());
        pic1.setTransferHandler(picHandler);
        mugshots.add(pic1);

        final DTPicture pic2 = new DTPicture(createImageIcon(ANYA + ".jpg", ANYA).getImage());
        pic2.setTransferHandler(picHandler);
        mugshots.add(pic2);

        final DTPicture pic3 = new DTPicture(createImageIcon(LAINE + ".jpg", LAINE).getImage());
        pic3.setTransferHandler(picHandler);
        mugshots.add(pic3);

        final DTPicture pic4 = new DTPicture(createImageIcon(COSMO + ".jpg", COSMO).getImage());
        pic4.setTransferHandler(picHandler);
        mugshots.add(pic4);

        final DTPicture pic5 = new DTPicture(createImageIcon(ADELE + ".jpg", ADELE).getImage());
        pic5.setTransferHandler(picHandler);
        mugshots.add(pic5);

        final DTPicture pic6 = new DTPicture(createImageIcon(ALEXI + ".jpg", ALEXI).getImage());
        pic6.setTransferHandler(picHandler);
        mugshots.add(pic6);

        // These six components with no pictures provide handy drop targets.
        final DTPicture pic7 = new DTPicture(null);
        pic7.setTransferHandler(picHandler);
        mugshots.add(pic7);

        final DTPicture pic8 = new DTPicture(null);
        pic8.setTransferHandler(picHandler);
        mugshots.add(pic8);

        final DTPicture pic9 = new DTPicture(null);
        pic9.setTransferHandler(picHandler);
        mugshots.add(pic9);

        final DTPicture pic10 = new DTPicture(null);
        pic10.setTransferHandler(picHandler);
        mugshots.add(pic10);

        final DTPicture pic11 = new DTPicture(null);
        pic11.setTransferHandler(picHandler);
        mugshots.add(pic11);

        final DTPicture pic12 = new DTPicture(null);
        pic12.setTransferHandler(picHandler);
        mugshots.add(pic12);

        setPreferredSize(new Dimension(450, 630));
        add(mugshots, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu mainMenu = new JMenu("Edit");
        mainMenu.setMnemonic(KeyEvent.VK_E);

        final TransferActionListener actionListener = new TransferActionListener();

        JMenuItem menuItem = new JMenuItem("Cut");
        menuItem.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic(KeyEvent.VK_T);
        mainMenu.add(menuItem);

        menuItem = new JMenuItem("Copy");
        menuItem.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic(KeyEvent.VK_C);
        mainMenu.add(menuItem);

        menuItem = new JMenuItem("Paste");
        menuItem.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic(KeyEvent.VK_P);
        mainMenu.add(menuItem);

        menuBar.add(mainMenu);

        return menuBar;
    }
}
