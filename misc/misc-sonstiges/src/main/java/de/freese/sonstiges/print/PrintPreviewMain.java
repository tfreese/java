package de.freese.sonstiges.print;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example for Print and Print-Preview.
 *
 * @author Thomas Freese
 */
public final class PrintPreviewMain extends JPanel implements Printable, ActionListener {
    private static final int BORDER_SIZE = 50;
    private static final Color COLOR_BACKGROUND = Color.darkGray;
    private static final Color COLOR_FOREGROUND = Color.black;
    private static final Color COLOR_FRAME = Color.lightGray;
    private static final Color COLOR_PAPER = Color.white;
    private static final String LABEL_MENU_ENTER_TEXT = "Text eingeben";
    private static final String LABEL_MENU_EXIT = "Beenden";
    private static final String LABEL_MENU_PAGE_LAYOUT = "Seite einrichten";
    private static final String LABEL_MENU_PRINT = "Drucken";
    private static final String LABEL_MENU_PRINTER = "Drucker einrichten";
    private static final String LABEL_MENU_ZOOM_IN = "Vergrössern";
    private static final String LABEL_MENU_ZOOM_OUT = "Verkleinern";
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintPreviewMain.class);
    @Serial
    private static final long serialVersionUID = -2189370102458478566L;

    public static void main(final String[] args) throws Exception {
        final JFrame frame = new JFrame("Einfaches Druckbeispiel");
        final PrintPreviewMain printPreview = new PrintPreviewMain();

        final WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        };

        frame.addWindowListener(listener);

        frame.getContentPane().add(printPreview);

        final JMenuBar menuBar = new JMenuBar();
        final JMenu menu = new JMenu("Menü");
        JMenuItem menuItem;

        menuItem = new JMenuItem(LABEL_MENU_ENTER_TEXT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(LABEL_MENU_ZOOM_IN);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(LABEL_MENU_ZOOM_OUT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(LABEL_MENU_PAGE_LAYOUT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(LABEL_MENU_PRINTER);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(LABEL_MENU_PRINT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(LABEL_MENU_EXIT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuBar.add(menu);

        frame.setJMenuBar(menuBar);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private final transient Image imgCup;
    private final transient Image imgDuke;
    private final transient PrinterJob printerJob;

    private double mdPreviewScale = 0.5D;
    private transient PageFormat pageFormat;
    private String text = "Drucken mit Java 2";

    private PrintPreviewMain() throws IOException {
        super();

        // Init Printer-Settings and Page-Layout.
        printerJob = PrinterJob.getPrinterJob();
        pageFormat = printerJob.defaultPage();

        setBackground(COLOR_BACKGROUND);

        setPreferredSize(new Dimension((int) ((pageFormat.getWidth() + (2 * BORDER_SIZE)) * mdPreviewScale),
                (int) ((pageFormat.getHeight() + (2 * BORDER_SIZE)) * mdPreviewScale)));

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/duke.png")) {
            imgDuke = ImageIO.read(inputStream);
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/java_cup.gif")) {
            imgCup = ImageIO.read(inputStream);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        if (event.getSource() instanceof JMenuItem) {
            if (LABEL_MENU_PRINT.equals(event.getActionCommand())) {
                print();
            }
            else if (LABEL_MENU_PAGE_LAYOUT.equals(event.getActionCommand())) {
                pageFormat = printerJob.pageDialog(pageFormat);
                repaint();
            }
            else if (LABEL_MENU_PRINTER.equals(event.getActionCommand())) {
                if (printerJob.printDialog()) {
                    pageFormat = printerJob.validatePage(pageFormat);
                    repaint();
                }
            }
            else if (LABEL_MENU_ZOOM_IN.equals(event.getActionCommand())) {
                if (mdPreviewScale < 2D) {
                    mdPreviewScale *= 2D;
                    repaint();
                }
            }
            else if (LABEL_MENU_ZOOM_OUT.equals(event.getActionCommand())) {
                if (mdPreviewScale > 0.25D) {
                    mdPreviewScale /= 2D;
                    repaint();
                }
            }
            else if (LABEL_MENU_ENTER_TEXT.equals(event.getActionCommand())) {
                enterText();
            }
            else if (LABEL_MENU_EXIT.equals(event.getActionCommand())) {
                System.exit(0);
            }
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;

        g2.scale(mdPreviewScale, mdPreviewScale);

        g2.setPaint(COLOR_PAPER);
        g2.fillRect(BORDER_SIZE, BORDER_SIZE, (int) pageFormat.getWidth(), (int) pageFormat.getHeight());

        g2.translate(BORDER_SIZE, BORDER_SIZE);

        g2.setPaint(COLOR_FRAME);

        g2.drawLine(0, (int) pageFormat.getImageableY() - 1, (int) pageFormat.getWidth() - 1, (int) pageFormat.getImageableY() - 1);
        g2.drawLine(0, (int) (pageFormat.getImageableY() + pageFormat.getImageableHeight()), (int) pageFormat.getWidth() - 1,
                (int) (pageFormat.getImageableY() + pageFormat.getImageableHeight()));
        g2.drawLine((int) pageFormat.getImageableX() - 1, 0, (int) pageFormat.getImageableX() - 1, (int) pageFormat.getHeight() - 1);
        g2.drawLine((int) (pageFormat.getImageableX() + pageFormat.getImageableWidth()), 0, (int) (pageFormat.getImageableX() + pageFormat.getImageableWidth()),
                (int) pageFormat.getHeight() - 1);

        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        g2.setClip(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

        drawMyGraphics(g2);
    }

    public void print() {
        // Get Page-Format and configure for Landscape.
        final PageFormat pfLandscape = printerJob.defaultPage();
        pfLandscape.setOrientation(PageFormat.LANDSCAPE);

        final Book book = new Book();

        // Create CoverPage.
        // Set Page-Count, see CoverPage#print Method.
        book.append(new CoverPage(), pfLandscape, 1);

        // Add Graphic from Preview.
        book.append(this, pageFormat);

        printerJob.setPageable(book);

        try {
            printerJob.print();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public int print(final Graphics g, final PageFormat pageFormat, final int pageIndex) throws PrinterException {
        int printState = Printable.NO_SUCH_PAGE;

        // PageIndex == 0 is the CoverPage!
        if (pageIndex == 1) {
            final Graphics2D g2 = (Graphics2D) g;

            g2.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
            g2.setClip(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

            drawMyGraphics(g2);

            printState = Printable.PAGE_EXISTS;
        }

        return printState;
    }

    private void drawMyGraphics(final Graphics2D g2) {
        g2.setPaint(COLOR_FOREGROUND);

        g2.setFont(g2.getFont().deriveFont(30F));
        g2.drawString(text, 20, 40);

        g2.drawImage(imgDuke, 10, 100, 200, 200, this);
        g2.drawImage(imgCup, 10, 350, 200, 200, this);
    }

    private void enterText() {
        final Object userInput = JOptionPane.showInputDialog(null, "Bitte einen Text eingeben", "Drucktext", JOptionPane.PLAIN_MESSAGE, null, null, text);

        if (userInput instanceof String t) {
            text = t;

            repaint(); // neu zeichnen
        }
    }
}
