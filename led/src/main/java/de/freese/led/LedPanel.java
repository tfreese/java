package de.freese.led;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.Serial;

import javax.swing.JPanel;
import javax.swing.Timer;

import de.freese.led.model.LedModel;
import de.freese.led.model.LedSymbol;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
class LedPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -1L;

    private final transient BufferedImage displayBuffer;
    private final int displayCols;
    private final LedModel ledModel;
    private final Timer timer;

    private int currentTextCol = 0;

    public LedPanel(final String text, final int ledRows, final int ledCols) {
        super();

        this.displayCols = ledCols;

        // Den Text in eine simple logische Boolean-Matrix umwandeln (true = LED an).
        ledModel = new LedModel(ledRows)
                .addSymbol(LedSymbol.ARROW_UP, Color.GREEN)
                .addSymbol(LedSymbol.ARROW_DOWN, Color.RED)
                .addText(text);

        setBackground(ledModel.getColorBackground());

        final int viewWidth = displayCols * ledModel.getColumnWidth();
        final int viewHeight = ledRows * ledModel.getColumnWidth();

        // Das einzige Bild erzeugen und mit ausgeschalteten LEDs füllen.
        displayBuffer = new BufferedImage(viewWidth, viewHeight, BufferedImage.TYPE_INT_ARGB);

        initDisplayWithOffLEDs();

        // Timer: Verschiebt das Bild um eine LED-Breite nach links.
        timer = new Timer(60, e -> {
            scrollAndRenderNextColumn();
            repaint();
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(displayCols * ledModel.getColumnWidth(), ledModel.rowCount() * ledModel.getColumnWidth());
    }

    public void startAnimation() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stopAnimation() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        g.drawImage(displayBuffer, 0, 0, null);

        // Synchronizing the painting on systems that buffer graphics events.
        // Without this line, the animation might not be smooth on Linux.
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    protected void printChildren(final Graphics g) {
        // There are no Child Components.
        // super.printChildren(g);
    }

    /**
     * Füllt das eine Bild initial komplett mit dunklen LEDs.
     */
    private void initDisplayWithOffLEDs() {
        final Graphics2D g = displayBuffer.createGraphics();
        g.setColor(ledModel.getColorBackground());
        g.fillRect(0, 0, displayBuffer.getWidth(), displayBuffer.getHeight());

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dunkles, mattes Rot.
        g.setColor(ledModel.getColorInactive());

        final int ledSize = ledModel.getLedSize();
        final int ledGap = ledModel.getLedGap();
        final int columnWidth = ledModel.getColumnWidth();

        for (int column = 0; column < displayCols; column++) {
            for (int row = 0; row < ledModel.rowCount(); row++) {
                g.fillOval(column * columnWidth + ledGap / 2, row * columnWidth + ledGap / 2, ledSize, ledSize);
            }
        }

        g.dispose();
    }

    /**
     * Der Kern: Verschiebt das eine Bild und zeichnet live am rechten Rand.
     */
    private void scrollAndRenderNextColumn() {
        final int ledSize = ledModel.getLedSize();
        final int ledGap = ledModel.getLedGap();
        final int columnWidth = ledModel.getColumnWidth();

        final Graphics2D g = displayBuffer.createGraphics();

        // 1. Bild auf sich selbst um eine Kachelbreite nach links verschieben.
        g.drawImage(displayBuffer, -columnWidth, 0, null);

        // 2. Den freigewordenen Streifen ganz rechts säubern.
        final int lastColX = displayBuffer.getWidth() - columnWidth;

        // Vorherige Pixel komplett verwerfen.
        g.setComposite(AlphaComposite.Src);
        g.setColor(ledModel.getColorBackground());
        g.fillRect(lastColX, 0, columnWidth, displayBuffer.getHeight());

        // 3. Nur die eine neue LED-Spalte live auf den rechten Rand zeichnen.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < ledModel.rowCount(); row++) {
            final boolean isLEDOn = ledModel.isActive(row, currentTextCol);

            // LED-Koordinaten exakt auf der neuen Spalte rechts berechnen.
            final int ledX = lastColX + ledGap / 2;
            final int ledY = row * columnWidth + ledGap / 2;

            if (isLEDOn) {
                final Color color = ledModel.getColorActive(currentTextCol);

                g.setColor(color);
                g.fillOval(ledX, ledY, ledSize, ledSize);

                // Mit hellem Kern.
                // g.setColor(color.brighter());
                // g.fillOval(ledX + 2, ledY + 2, ledSize / 2, ledSize / 2);
            }
            else {
                g.setColor(ledModel.getColorInactive());
                g.fillOval(ledX, ledY, ledSize, ledSize);
            }
        }

        g.dispose();

        // Zeiger in der Text-Matrix weiterbewegen.
        currentTextCol++;

        if (currentTextCol >= ledModel.columnCount()) {
            // Text wiederholen.
            currentTextCol = 0;
        }
    }
}