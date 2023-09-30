package net.ledticker.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.led.elements.StockDisplayElement;
import net.led.provider.Stock;
import net.led.provider.UpdateListener;
import net.led.provider.YahooProvider;
import net.led.util.ColorSelectorListener;
import net.led.util.ColorSelectorPanel;
import net.ledticker.LedTicker;
import net.ledticker.LedTickerFactory;

/**
 * @author Thomas Freese
 */
public class LedTickerDemo implements ActionListener, ChangeListener, UpdateListener, ColorSelectorListener {
    public static void main(final String[] args) {
        new LedTickerDemo();
    }

    private final JTextField dotHeight = new JTextField(6);

    private final JTextField dotWidth = new JTextField(6);

    private final Map<String, StockDisplayElement> elements = new HashMap<>();

    private final JTextField hGap = new JTextField(6);
    private final JSlider slider = new JSlider(1, 10);
    private final JTextField stocksField = new JTextField(6);
    private final JTextField vGap = new JTextField(6);
    private final YahooProvider yahooProvider;
    private LedTicker ledTicker;
    private Color stockDownColor = Color.RED;
    private Color stockNeutralColor = Color.YELLOW;
    private Color stockUpColor = Color.GREEN;
    private Color symbolColor = Color.YELLOW;
    private JFrame tickerFrame;

    public LedTickerDemo() {
        super();

        createLedTickerComponent();
        createGUI();

        // Create the YahooProvider.
        this.yahooProvider = new YahooProvider();

        String[] initialSymbols = {"MSFT", "INTC", "DELL", "GOOG", "ORCL", "AMZN", "GE", "JNJ", "PG", "WMT", "HD"};
        for (String initialSymbol : initialSymbols) {
            addSymbol(initialSymbol);
        }

        this.yahooProvider.addUpdateListener(this);
        this.yahooProvider.start();

        // Start the ticker animation
        this.ledTicker.startAnimation();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String command = e.getActionCommand();

        if ("addStock".equals(command)) {
            String name = this.stocksField.getText().toUpperCase();
            this.stocksField.setText(name);
            addSymbol(name);
        }
        else if ("removeStock".equals(command)) {
            String name = this.stocksField.getText().toUpperCase();
            this.stocksField.setText(name);
            StockDisplayElement ste = this.elements.get(name);

            if (ste != null) {
                this.yahooProvider.removeSymbol(name);
                this.ledTicker.removeElement(ste);
                this.elements.remove(name);
            }
        }
        else if ("removeAll".equals(command)) {
            this.yahooProvider.removeAllElements();
            this.elements.clear();

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch (InterruptedException ex) {
                // Ignore
            }

            this.ledTicker.removeAll();
        }
        else if ("pause".equals(command)) {
            this.ledTicker.pauseAnimation();
        }
        else if ("start".equals(command)) {
            this.ledTicker.startAnimation();
        }
        else if ("stop".equals(command)) {
            this.ledTicker.stopAnimation();
        }
        else if ("setSpeed".equals(command)) {
            this.ledTicker.setSpeed(this.slider.getValue());
        }
        else if ("setDotSize".equals(command)) {
            int width, height = 0;

            try {
                width = Integer.parseInt(this.dotWidth.getText(), 10);
            }
            catch (Exception ex) {
                width = 1;
            }

            try {
                height = Integer.parseInt(this.dotHeight.getText(), 10);
            }
            catch (Exception ex) {
                height = 1;
            }

            this.ledTicker.setDotSize(width, height);
            this.tickerFrame.pack();
            centerFrame(this.tickerFrame);
        }
        else if ("setDotGap".equals(command)) {
            int hg, vg = 0;

            try {
                hg = Integer.parseInt(this.hGap.getText(), 10);
            }
            catch (Exception ex) {
                hg = 1;
            }

            try {
                vg = Integer.parseInt(this.vGap.getText(), 10);
            }
            catch (Exception ex) {
                vg = 1;
            }

            this.ledTicker.setDotGaps(hg, vg);
            this.tickerFrame.pack();
            centerFrame(this.tickerFrame);
        }
    }

    @Override
    public void setColor(final String id, final Color color) {
        if ("bgColor".equals(id)) {
            this.ledTicker.setBackgroundColor(color);
        }
        else if ("gridColor".equals(id)) {
            this.ledTicker.setDotOffColor(color);
        }
        else if ("symbolColor".equals(id)) {
            this.symbolColor = color;

            for (StockDisplayElement ste : this.elements.values()) {
                ste.setSymbolColor(color);
            }

            this.ledTicker.updateAll();
        }
        else if ("upColor".equals(id)) {
            this.stockUpColor = color;

            for (StockDisplayElement ste : this.elements.values()) {
                ste.setStockUpColor(color);
            }

            this.ledTicker.updateAll();
        }
        else if ("neutralColor".equals(id)) {
            this.stockNeutralColor = color;

            for (StockDisplayElement ste : this.elements.values()) {
                ste.setStockNeutralColor(color);
            }

            this.ledTicker.updateAll();
        }
        else if ("downColor".equals(id)) {
            this.stockDownColor = color;

            for (StockDisplayElement ste : this.elements.values()) {
                ste.setStockDownColor(color);
            }

            this.ledTicker.updateAll();
        }
    }

    @Override
    public void stateChanged(final ChangeEvent event) {
        this.ledTicker.setSpeed(((JSlider) event.getSource()).getValue());
    }

    @Override
    public void update(final Object newValue) {
        Stock stock = (Stock) newValue;

        StockDisplayElement ste = this.elements.get(stock.getID());

        if (ste != null) {
            ste.setLast(stock.getLast());
            ste.setChangePercent(stock.getChangePercent());
            this.ledTicker.update(ste);
        }
    }

    private void addSymbol(final String symbol) {
        StockDisplayElement ste = new StockDisplayElement(symbol);
        ste.setStockUpColor(this.stockUpColor);
        ste.setStockNeutralColor(this.stockNeutralColor);
        ste.setStockDownColor(this.stockDownColor);
        ste.setSymbolColor(this.symbolColor);
        this.elements.put(symbol, ste);
        this.yahooProvider.addSymbol(symbol);
        this.ledTicker.addElement(ste);
    }

    private void centerFrame(final Window frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

    private JPanel createDotsControlsPanel() {
        TitledBorder dotsControlsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        dotsControlsBorder.setTitle("Dots Controls");

        JLabel dotWidthLabel = new JLabel("Dot width");
        JLabel dotHeightLabel = new JLabel("Dot height");
        JButton setDotSize = new JButton("Set Dot size");
        setDotSize.setActionCommand("setDotSize");
        setDotSize.addActionListener(this);

        JPanel dotsControlsPanel = new JPanel(new GridBagLayout());
        dotsControlsPanel.setBorder(dotsControlsBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 0, 5, 5);

        dotsControlsPanel.add(this.dotWidth, gbc);

        gbc.gridx++;
        dotsControlsPanel.add(dotWidthLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        dotsControlsPanel.add(this.dotHeight, gbc);

        gbc.gridx++;
        dotsControlsPanel.add(dotHeightLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dotsControlsPanel.add(setDotSize, gbc);

        return dotsControlsPanel;
    }

    private void createGUI() {
        JPanel generalPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.gridwidth = 2;
        generalPanel.add(createScrollControlsPanel(), gbc);

        gbc.gridy = 1;
        gbc.gridheight = 2;
        generalPanel.add(createStocksControlsPanel(), gbc);

        gbc.gridx += 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        generalPanel.add(createTickerPaneColors(), gbc);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.weightx = 0.5;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        generalPanel.add(createDotsControlsPanel(), gbc);

        gbc.gridx++;
        generalPanel.add(createGapsControlsPanel(), gbc);

        gbc.gridx++;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        generalPanel.add(createTrendColorsPanel(), gbc);

        // ADD TICKER COMPONENT
        this.tickerFrame = new JFrame("Led Ticker Component v2.0");
        this.tickerFrame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints tickerFrameConstarints = new GridBagConstraints();

        tickerFrameConstarints.gridx = 0;
        tickerFrameConstarints.gridy = 0;
        tickerFrameConstarints.weightx = 1;
        tickerFrameConstarints.weighty = 0;
        tickerFrameConstarints.insets = new Insets(5, 5, 0, 5);
        tickerFrameConstarints.fill = GridBagConstraints.HORIZONTAL;
        this.tickerFrame.getContentPane().add(this.ledTicker.getTickerComponent(), tickerFrameConstarints);

        // ADD THE MAIN PANEL OF THIS DEMO
        tickerFrameConstarints.fill = GridBagConstraints.NONE;
        tickerFrameConstarints.anchor = GridBagConstraints.NORTH;
        tickerFrameConstarints.gridy = 1;
        tickerFrameConstarints.gridx = 0;
        tickerFrameConstarints.weightx = 0;
        tickerFrameConstarints.weighty = 1;
        this.tickerFrame.getContentPane().add(generalPanel, tickerFrameConstarints);

        this.tickerFrame.pack();
        centerFrame(this.tickerFrame);
        this.tickerFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.tickerFrame.setVisible(true);
    }

    private JPanel createGapsControlsPanel() {
        TitledBorder gapsControlsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        gapsControlsBorder.setTitle("Gaps Controls");

        JLabel hGapLabel = new JLabel("Horizontal gap");
        JLabel vGapLabel = new JLabel("Vertical gap");
        JButton setDotGap = new JButton("Set Dot Gap");
        setDotGap.setActionCommand("setDotGap");
        setDotGap.addActionListener(this);

        JPanel gapsControlsPanel = new JPanel(new GridBagLayout());
        gapsControlsPanel.setBorder(gapsControlsBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 0, 5, 5);

        gapsControlsPanel.add(this.hGap, gbc);

        gbc.gridx++;
        gapsControlsPanel.add(hGapLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gapsControlsPanel.add(this.vGap, gbc);

        gbc.gridx++;
        gapsControlsPanel.add(vGapLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gapsControlsPanel.add(setDotGap, gbc);

        return gapsControlsPanel;
    }

    private void createLedTickerComponent() {
        // STEP1 : create the component
        this.ledTicker = LedTickerFactory.createLedTicker();
        this.ledTicker.setElementGap(4);
        this.ledTicker.setTokenGap(2);
        this.ledTicker.setDotSize(2, 2);
        this.ledTicker.setDotGaps(1, 1);

    }

    private JPanel createScrollControlsPanel() {
        TitledBorder scrollControlsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        scrollControlsBorder.setTitle("Ticker Controls");

        JButton pause = new JButton("Pause");
        pause.setActionCommand("pause");
        pause.addActionListener(this);

        JButton start = new JButton("Start");
        start.setActionCommand("start");
        start.addActionListener(this);

        JButton stop = new JButton("Stop");
        stop.setActionCommand("stop");
        stop.addActionListener(this);

        this.slider.addChangeListener(this);
        this.slider.setPaintTicks(true);
        this.slider.setPaintLabels(true);
        this.slider.setMajorTickSpacing(3);
        this.slider.setMinorTickSpacing(1);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();

        for (int i = this.slider.getMinimum(); i <= this.slider.getMaximum(); i++) {
            labels.put(i, new JLabel(String.valueOf(i)));
        }
        this.slider.setLabelTable(labels);

        JPanel scrollControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
        scrollControlsPanel.setBorder(scrollControlsBorder);
        scrollControlsPanel.add(start);
        scrollControlsPanel.add(stop);
        scrollControlsPanel.add(pause);
        scrollControlsPanel.add(this.slider);

        return scrollControlsPanel;
    }

    private JPanel createStocksControlsPanel() {
        TitledBorder stocksControlsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        stocksControlsBorder.setTitle("Stocks Controls");

        JButton addStock = new JButton("Add Stock");
        addStock.setActionCommand("addStock");
        addStock.addActionListener(this);

        this.stocksField.setActionCommand("addStock");
        this.stocksField.addActionListener(this);

        JButton removeStock = new JButton("Remove Stock");
        removeStock.setActionCommand("removeStock");
        removeStock.addActionListener(this);

        JButton removeAllStocks = new JButton("Remove All");
        removeAllStocks.setActionCommand("removeAll");
        removeAllStocks.addActionListener(this);

        JPanel stocksControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
        stocksControlsPanel.setBorder(stocksControlsBorder);
        stocksControlsPanel.add(addStock);
        stocksControlsPanel.add(this.stocksField);
        stocksControlsPanel.add(removeStock);
        stocksControlsPanel.add(removeAllStocks);

        return stocksControlsPanel;
    }

    private JPanel createTickerPaneColors() {
        TitledBorder stocksColorsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        stocksColorsBorder.setTitle("Ticker Colors");

        Color gridColor = new Color(0x111111);
        Color bgColor = new Color(0x333333);

        ColorSelectorPanel backgroundSelectorPanel = new ColorSelectorPanel("Select background color", bgColor, "bgColor", this);
        ColorSelectorPanel gridSelectorPanel = new ColorSelectorPanel("Select turned-off led color", gridColor, "gridColor", this);
        ColorSelectorPanel symbolSelectorPanel = new ColorSelectorPanel("Select symbol color", this.symbolColor, "symbolColor", this);

        JPanel stocksColorsPanel = new JPanel(new GridLayout(3, 1));
        stocksColorsPanel.setBorder(stocksColorsBorder);
        stocksColorsPanel.add(backgroundSelectorPanel);
        stocksColorsPanel.add(gridSelectorPanel);
        stocksColorsPanel.add(symbolSelectorPanel);

        this.ledTicker.setBackgroundColor(bgColor);
        this.ledTicker.setDotOffColor(gridColor);

        return stocksColorsPanel;
    }

    private JPanel createTrendColorsPanel() {
        TitledBorder trendColorsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        trendColorsBorder.setTitle("Trend Colors");

        ColorSelectorPanel stockUpSelectorPanel = new ColorSelectorPanel("Select Stock Up Color", this.stockUpColor, "upColor", this);
        ColorSelectorPanel stockNeutralSelectorPanel = new ColorSelectorPanel("Select Stock Neutral Color", this.stockNeutralColor, "neutralColor", this);
        ColorSelectorPanel stockDownSelectorPanel = new ColorSelectorPanel("Select Stock Down Color", this.stockDownColor, "downColor", this);

        JPanel trendColorsPanel = new JPanel(new GridLayout(3, 1));
        trendColorsPanel.setBorder(trendColorsBorder);
        trendColorsPanel.add(stockUpSelectorPanel);
        trendColorsPanel.add(stockNeutralSelectorPanel);
        trendColorsPanel.add(stockDownSelectorPanel);

        return trendColorsPanel;
    }
}
