package net.ledticker.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.led.elements.StockDisplayElement;
import net.led.provider.Stock;
import net.led.provider.UpdateListener;
import net.led.provider.YahooProvider;
import net.led.util.OptionsDialog;
import net.led.util.OptionsListener;
import net.ledticker.LedTicker;
import net.ledticker.LedTickerFactory;

/**
 * @author Thomas Freese
 */
public class SimpleTickerDemo implements ActionListener, OptionsListener, UpdateListener {
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            // Ignore
        }

        new SimpleTickerDemo();
    }

    private final Map<String, StockDisplayElement> elements = new HashMap<>();
    private final YahooProvider yahooProvider;

    private LedTicker ledTicker;
    private JPopupMenu menu;
    private JFrame tickerFrame;

    public SimpleTickerDemo() {
        super();

        createLedTickerComponent();
        createPopUpMenu();
        createGUI();

        // Create a YahooProvider.
        this.yahooProvider = new YahooProvider();

        for (String element : this.elements.keySet()) {
            this.yahooProvider.addSymbol(element);
        }

        this.yahooProvider.addUpdateListener(this);
        this.yahooProvider.start();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final String command = e.getActionCommand();

        if ("menuOptions".equals(command)) {
            this.menu.setVisible(false);
            final String[] symbols = new String[this.elements.size()];
            int i = 0;

            for (String string : this.elements.keySet()) {
                symbols[i++] = string;
            }

            new OptionsDialog(this.tickerFrame, this, symbols);
        }
        else if ("moveUp".equals(command)) {
            this.menu.setVisible(false);
            // Move the ticker to the top of the screen.
            this.tickerFrame.setLocation(0, 0);
        }
        else if ("moveDown".equals(command)) {
            this.menu.setVisible(false);
            // Move the ticker 100 pixels above the bottom of the screen.
            // We added 100 pixels to avoid moving the ticker under the Windows taskbar.
            this.tickerFrame.setLocation(0, Toolkit.getDefaultToolkit().getScreenSize().height - 100);
        }
        else if ("exit".equals(command)) {
            System.exit(0);
        }
    }

    @Override
    public void addSymbol(final String symbol) {
        final StockDisplayElement ste = new StockDisplayElement(symbol);
        this.elements.put(symbol, ste);
        this.ledTicker.addElement(ste);
        this.yahooProvider.addSymbol(symbol);
    }

    @Override
    public void removeSymbol(final String symbol) {
        final StockDisplayElement ste = this.elements.get(symbol);

        if (ste != null) {
            this.yahooProvider.removeSymbol(symbol);
            this.ledTicker.removeElement(ste);
            this.elements.remove(symbol);
        }
    }

    @Override
    public void update(final Object newValue) {
        final Stock stock = (Stock) newValue;

        final StockDisplayElement ste = this.elements.get(stock.getID());

        if (ste != null) {
            ste.setLast(stock.getLast());
            ste.setChangePercent(stock.getChangePercent());
            this.ledTicker.update(ste);
        }
    }

    private void createGUI() {
        // ADD TICKER COMPONENT
        this.tickerFrame = new JFrame("Led Ticker Component v2.0");

        this.tickerFrame.getContentPane().setLayout(new GridBagLayout());

        final GridBagConstraints tickerFrameConstarints = new GridBagConstraints();
        tickerFrameConstarints.gridx = 0;
        tickerFrameConstarints.gridy = 0;
        tickerFrameConstarints.weightx = 1;
        tickerFrameConstarints.weighty = 0;
        tickerFrameConstarints.insets = new Insets(0, 0, 0, 0);
        tickerFrameConstarints.fill = GridBagConstraints.HORIZONTAL;

        this.tickerFrame.getContentPane().add(this.ledTicker.getTickerComponent(), tickerFrameConstarints);

        this.tickerFrame.setUndecorated(true);
        this.tickerFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize().width, this.tickerFrame.getPreferredSize().height);
        this.tickerFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.tickerFrame.setVisible(true);
        this.ledTicker.startAnimation();
    }

    private void createLedTickerComponent() {
        // STEP1 : create the component
        this.ledTicker = LedTickerFactory.createLedTicker();
        this.ledTicker.setElementGap(12);
        this.ledTicker.setTokenGap(6);
        this.ledTicker.setDotSize(3, 3);
        this.ledTicker.setDotGaps(1, 1);

        final String[] initialSymbols = {"MSFT", "INTC", "DELL", "GOOG", "ORCL", "AMZN", "GE", "JNJ", "PG", "WMT", "HD"};

        for (String initialSymbol : initialSymbols) {
            final StockDisplayElement ste = new StockDisplayElement(initialSymbol);
            this.elements.put(ste.getSymbol(), ste);
            this.ledTicker.addElement(ste);
        }
    }

    private void createPopUpMenu() {
        final JMenuItem optionsMenuItem = new JMenuItem("Options");
        optionsMenuItem.setActionCommand("menuOptions");
        optionsMenuItem.addActionListener(this);

        final JMenuItem moveUpMenuItem = new JMenuItem("Move to screen top");
        moveUpMenuItem.setActionCommand("moveUp");
        moveUpMenuItem.addActionListener(this);

        final JMenuItem moveDownMenuItem = new JMenuItem("Move to screen bottom");
        moveDownMenuItem.setActionCommand("moveDown");
        moveDownMenuItem.addActionListener(this);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setActionCommand("exit");
        exitMenuItem.addActionListener(this);

        this.menu = new JPopupMenu();

        this.menu.add(optionsMenuItem);
        this.menu.addSeparator();
        this.menu.add(moveUpMenuItem);
        this.menu.add(moveDownMenuItem);
        this.menu.addSeparator();
        this.menu.add(exitMenuItem);

        this.ledTicker.getTickerComponent().addMouseListener((new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                if (event.isPopupTrigger()) {
                    SimpleTickerDemo.this.menu.show(event.getComponent(), event.getX(), event.getY());
                }
            }

            @Override
            public void mouseReleased(final MouseEvent event) {
                if (event.isPopupTrigger()) {
                    SimpleTickerDemo.this.menu.show(event.getComponent(), event.getX(), event.getY());
                }
            }
        }));
    }
}
