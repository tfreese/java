package net.leddisplay.demo;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import net.led.elements.TimeDisplayElement;
import net.led.provider.TimeProvider;
import net.led.provider.UpdateListener;
import net.led.util.ColorSelectorListener;
import net.led.util.ColorSelectorPanel;
import net.leddisplay.LedDisplay;
import net.leddisplay.LedDisplayFactory;

/**
 * @author Thomas Freese
 */
public class TimeDisplayDemo implements ActionListener, ColorSelectorListener, UpdateListener, ItemListener {
    public static void main(final String[] args) {
        new TimeDisplayDemo();
    }

    private final JComboBox<String> anchor = new JComboBox<>();
    private final JTextField bottomLead = new JTextField(6);
    private final TimeDisplayElement displayElement;
    private final JTextField dotHeight = new JTextField(6);
    private final JTextField dotWidth = new JTextField(6);
    private final JTextField hGap = new JTextField(6);
    private final JTextField leftLead = new JTextField(6);
    private final JTextField rightLead = new JTextField(6);
    private final Color stockDownColor = Color.RED;
    private final Color stockNeutralColor = Color.YELLOW;
    private final Color stockUpColor = Color.GREEN;
    private final Color symbolColor = Color.YELLOW;
    private final TimeProvider timeProvider;
    private final JTextField topLead = new JTextField(6);
    private final JTextField vGap = new JTextField(6);

    private JFrame displayFrame;
    private LedDisplay ledDisplay;

    public TimeDisplayDemo() {
        super();

        createLedDisplayComponent();

        // create an TextDisplayElement and add it to the display
        final String symbol = "Zeit";
        this.displayElement = new TimeDisplayElement(symbol);
        this.ledDisplay.setDisplayElement(this.displayElement);
        createGUI();

        // create Provider
        this.timeProvider = new TimeProvider();
        this.timeProvider.addSymbol(symbol);
        this.timeProvider.addUpdateListener(this);
        this.timeProvider.start();
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();

        if ("setDotSize".equals(command)) {
            int width = 0;
            int height = 0;

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

            this.ledDisplay.setDotSize(width, height);

            // centerFrame(displayFrame);
        }
        else if ("setDotGap".equals(command)) {
            int hg = 0;
            int vg = 0;

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

            this.ledDisplay.setDotGaps(hg, vg);

            // centerFrame(displayFrame);
        }
        else if ("setPadding".equals(command)) {
            int top = 0;
            int left = 0;
            int bottom = 0;
            int right = 0;

            try {
                top = Integer.parseInt(this.topLead.getText(), 10);
            }
            catch (Exception ex) {
                // Empty
            }

            try {
                left = Integer.parseInt(this.leftLead.getText(), 10);
            }
            catch (Exception ex) {
                // Empty
            }

            try {
                bottom = Integer.parseInt(this.bottomLead.getText(), 10);
            }
            catch (Exception ex) {
                // Empty
            }

            try {
                right = Integer.parseInt(this.rightLead.getText(), 10);
            }
            catch (Exception ex) {
                // Empty
            }

            this.ledDisplay.setPadding(top, left, bottom, right);
        }
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        this.ledDisplay.setAnchor(this.anchor.getSelectedIndex());
    }

    @Override
    public void setColor(final String id, final Color color) {
        if ("bgColor".equals(id)) {
            this.ledDisplay.setBackgroundColor(color);
        }
        else if ("gridColor".equals(id)) {
            this.ledDisplay.setDotOffColor(color);
        }
    }

    @Override
    public void update(final Object newValue) {
        this.displayElement.setTime(newValue);
        this.ledDisplay.update();
    }

    private void centerFrame(final Window frame) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension frameSize = frame.getSize();
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

    private JPanel createAnchorControlPanel() {
        final TitledBorder anchorControlBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        anchorControlBorder.setTitle("Anchor Control");

        this.anchor.addItem("Center");
        this.anchor.addItem("North");
        this.anchor.addItem("West");
        this.anchor.addItem("East");
        this.anchor.addItem("South");
        this.anchor.addItem("Nortwest");
        this.anchor.addItem("Norteast");
        this.anchor.addItem("Southwest");
        this.anchor.addItem("Southeast");
        this.anchor.addItemListener(this);

        final JLabel label = new JLabel("Display Anchor");

        final JPanel stocksControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 1));
        stocksControlsPanel.setBorder(anchorControlBorder);
        stocksControlsPanel.add(label);
        stocksControlsPanel.add(this.anchor);

        return stocksControlsPanel;
    }

    private JPanel createDisplayPaneColors() {
        final TitledBorder displayColorsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        displayColorsBorder.setTitle("Display Colors");

        final Color gridColor = new Color(0x111111);
        final Color bgColor = new Color(0x333333);

        final ColorSelectorPanel backgroundSelectorPanel = new ColorSelectorPanel("Select background color", bgColor, "bgColor", this);
        final ColorSelectorPanel gridSelectorPanel = new ColorSelectorPanel("Select turned-off led color", gridColor, "gridColor", this);
        final ColorSelectorPanel symbolSelectorPanel = new ColorSelectorPanel("Select symbol color", this.symbolColor, "symbolColor", this);

        final JPanel textColorsPanel = new JPanel(new GridLayout(3, 1));
        textColorsPanel.setBorder(displayColorsBorder);
        textColorsPanel.add(backgroundSelectorPanel);
        textColorsPanel.add(gridSelectorPanel);
        textColorsPanel.add(symbolSelectorPanel);

        this.ledDisplay.setBackgroundColor(bgColor);
        this.ledDisplay.setDotOffColor(gridColor);

        return textColorsPanel;
    }

    private JPanel createDotsControlsPanel() {
        final TitledBorder dotsControlsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        dotsControlsBorder.setTitle("Dots Controls");

        this.dotWidth.setText("2");
        this.dotHeight.setText("2");

        final JLabel dotWidthLabel = new JLabel("Dot width");
        final JLabel dotHeightLabel = new JLabel("Dot height");
        final JButton setDotSize = new JButton("Set Dot size");
        setDotSize.setActionCommand("setDotSize");
        setDotSize.addActionListener(this);

        final JPanel dotsControlsPanel = new JPanel(new GridBagLayout());
        dotsControlsPanel.setBorder(dotsControlsBorder);

        final GridBagConstraints gbc = new GridBagConstraints();
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
        final JPanel generalPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.gridwidth = 2;
        generalPanel.add(createTrendColorsPanel(), gbc);

        gbc.gridy = 1;
        gbc.gridheight = 2;
        generalPanel.add(createAnchorControlPanel(), gbc);

        gbc.gridx += 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        generalPanel.add(createDisplayPaneColors(), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        generalPanel.add(createPaddingControlsPanel(), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        generalPanel.add(createDotsControlsPanel(), gbc);

        gbc.gridx++;
        generalPanel.add(createGapsControlsPanel(), gbc);

        // ADD TICKER COMPONENT
        this.displayFrame = new JFrame("Led Ticker Component v2.0");
        this.displayFrame.getContentPane().setLayout(new GridBagLayout());

        final GridBagConstraints tickerFrameConstraints = new GridBagConstraints();
        tickerFrameConstraints.gridx = 0;
        tickerFrameConstraints.gridy = 0;
        tickerFrameConstraints.weightx = 1;
        tickerFrameConstraints.weighty = 1;
        tickerFrameConstraints.insets = new Insets(5, 5, 0, 5);
        tickerFrameConstraints.fill = GridBagConstraints.BOTH;
        this.displayFrame.getContentPane().add(this.ledDisplay.getComponent(), tickerFrameConstraints);

        // ADD THE MAIN PANEL OF THIS DEMO
        tickerFrameConstraints.fill = GridBagConstraints.NONE;
        tickerFrameConstraints.anchor = GridBagConstraints.NORTH;
        tickerFrameConstraints.gridy = 1;
        tickerFrameConstraints.gridx = 0;
        tickerFrameConstraints.weightx = 0;
        tickerFrameConstraints.weighty = 1;
        this.displayFrame.getContentPane().add(generalPanel, tickerFrameConstraints);

        // displayFrame.pack();
        this.displayFrame.setSize(600, 400);
        centerFrame(this.displayFrame);
        this.displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.displayFrame.setVisible(true);
    }

    private JPanel createGapsControlsPanel() {
        final TitledBorder gapsControlsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        gapsControlsBorder.setTitle("Gaps Controls");

        this.hGap.setText("1");
        this.vGap.setText("1");

        final JLabel hGapLabel = new JLabel("Horizontal gap");
        final JLabel vGapLabel = new JLabel("Vertical gap");
        final JButton setDotGap = new JButton("Set Dot Gap");
        setDotGap.setActionCommand("setDotGap");
        setDotGap.addActionListener(this);

        final JPanel gapsControlsPanel = new JPanel(new GridBagLayout());
        gapsControlsPanel.setBorder(gapsControlsBorder);

        final GridBagConstraints gbc = new GridBagConstraints();
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

    private void createLedDisplayComponent() {
        // STEP1 : create the component
        this.ledDisplay = LedDisplayFactory.createLedDisplay();
        this.ledDisplay.setTokenGap(2);
        this.ledDisplay.setDotSize(2, 2);
        this.ledDisplay.setDotGaps(1, 1);
    }

    private JPanel createPaddingControlsPanel() {
        final TitledBorder paddingControlsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        paddingControlsBorder.setTitle("Padding Controls");

        this.topLead.setText("0");
        this.leftLead.setText("0");
        this.bottomLead.setText("0");
        this.rightLead.setText("0");

        final JLabel topLabel = new JLabel("Top lead");
        final JLabel leftLabel = new JLabel("Left lead");
        final JLabel bottomLabel = new JLabel("Bottom lead");
        final JLabel rightLabel = new JLabel("Right lead");
        final JButton setPadding = new JButton("Set Padding");
        setPadding.setActionCommand("setPadding");
        setPadding.addActionListener(this);

        final JPanel paddingControlsPanel = new JPanel(new GridBagLayout());
        paddingControlsPanel.setBorder(paddingControlsBorder);

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 0, 5, 5);
        paddingControlsPanel.add(this.topLead, gbc);

        gbc.gridx++;
        paddingControlsPanel.add(topLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        paddingControlsPanel.add(this.leftLead, gbc);

        gbc.gridx++;
        paddingControlsPanel.add(leftLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        paddingControlsPanel.add(this.bottomLead, gbc);

        gbc.gridx++;
        paddingControlsPanel.add(bottomLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        paddingControlsPanel.add(this.rightLead, gbc);

        gbc.gridx++;
        paddingControlsPanel.add(rightLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        paddingControlsPanel.add(setPadding, gbc);

        return paddingControlsPanel;
    }

    private JPanel createTrendColorsPanel() {
        final TitledBorder trendColorsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
        trendColorsBorder.setTitle("Trend Colors");

        final ColorSelectorPanel stockUpSelectorPanel = new ColorSelectorPanel("Select Stock Up Color", this.stockUpColor, "upColor", this);
        final ColorSelectorPanel stockNeutralSelectorPanel = new ColorSelectorPanel("Select Stock Neutral Color", this.stockNeutralColor, "neutralColor", this);
        final ColorSelectorPanel stockDownSelectorPanel = new ColorSelectorPanel("Select Stock Down Color", this.stockDownColor, "downColor", this);

        final JPanel trendColorsPanel = new JPanel(new GridLayout(3, 1));
        trendColorsPanel.setBorder(trendColorsBorder);
        trendColorsPanel.add(stockUpSelectorPanel);
        trendColorsPanel.add(stockNeutralSelectorPanel);
        trendColorsPanel.add(stockDownSelectorPanel);

        return trendColorsPanel;
    }
}
