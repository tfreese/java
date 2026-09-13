package de.freese.led2;

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

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
class LedPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -1L;

    private static Color getColorBrighter(final Color color) {
        // fraction bestimmt, wie nah die Farbe an Weiß herangerückt wird (0.0 = original, 1.0 = reinweiß)
        final double fraction = 0.7D;

        final int r = (int) Math.round(color.getRed() + (255 - color.getRed()) * fraction);
        final int g = (int) Math.round(color.getGreen() + (255 - color.getGreen()) * fraction);
        final int b = (int) Math.round(color.getBlue() + (255 - color.getBlue()) * fraction);

        return new Color(r, g, b, color.getAlpha());
    }

    // DAS EINZIGE BILD (exakt Bildschirmgröße).
    private final transient BufferedImage displayBuffer;
    private final int displayCols;
    private final int displayRows;
    private final int ledGap = 2;
    private final LedModel ledModel;
    // LED-Raster-Dimensionen
    private final int ledSize = 8;
    private final int tileSize = ledSize + ledGap; // z.B. 10 Pixel.
    private final Timer timer;
    // Zeiger, welche Textspalte als Nächstes rechts "reinwandert".
    private int currentTextCol = 0;

    public LedPanel(final String text, final int ledRows, final int ledCols) {
        super();

        this.displayRows = ledRows;
        this.displayCols = ledCols;

        // Den Text in eine simple logische Boolean-Matrix umwandeln (true = LED an).
        ledModel = new LedModel().addText(text, displayRows);

        setBackground(ledModel.getColorBackground());

        final int viewWidth = displayCols * tileSize;
        final int viewHeight = displayRows * tileSize;

        // Das einzige Bild erzeugen und mit ausgeschalteten LEDs füllen.
        displayBuffer = new BufferedImage(viewWidth, viewHeight, BufferedImage.TYPE_INT_ARGB);

        initDisplayWithOffLEDs();

        // Timer: Verschiebt das Bild alle 60ms um eine LED-Breite nach links.
        timer = new Timer(500, e -> {
            scrollAndRenderNextColumn();
            repaint();
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(displayCols * tileSize, displayRows * tileSize);
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

        // Absolut minimaler Zeichenaufwand für Swing.
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

        for (int c = 0; c < displayCols; c++) {
            for (int r = 0; r < displayRows; r++) {
                g.fillOval(c * tileSize + ledGap / 2, r * tileSize + ledGap / 2, ledSize, ledSize);
            }
        }

        g.dispose();
    }

    /**
     * Der Kern: Verschiebt das eine Bild und zeichnet live am rechten Rand.
     */
    private void scrollAndRenderNextColumn() {
        final Graphics2D g = displayBuffer.createGraphics();

        // 1. Bild auf sich selbst um eine Kachelbreite nach links verschieben.
        g.drawImage(displayBuffer, -tileSize, 0, null);

        // 2. Den freigewordenen Streifen ganz rechts säubern.
        final int lastColX = displayBuffer.getWidth() - tileSize;

        // Vorherige Pixel komplett verwerfen.
        g.setComposite(AlphaComposite.Src);
        g.setColor(ledModel.getColorBackground());
        g.fillRect(lastColX, 0, tileSize, displayBuffer.getHeight());

        // 3. Nur die eine neue LED-Spalte live auf den rechten Rand zeichnen.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < displayRows; r++) {
            final boolean isLEDOn = ledModel.isActive(r, currentTextCol);

            // LED-Koordinaten exakt auf der neuen Spalte rechts berechnen.
            final int ledX = lastColX + ledGap / 2;
            final int ledY = r * tileSize + ledGap / 2;

            if (isLEDOn) {
                // LED AN: Leuchtend Rot.
                final Color color = ledModel.getColorActive(currentTextCol);

                g.setColor(color);
                g.fillOval(ledX, ledY, ledSize, ledSize);

                // Mit hellem Kern.
                // g.setColor(new Color(255, 180, 180));
                g.setColor(getColorBrighter(color));
                g.fillOval(ledX + 2, ledY + 2, ledSize / 2, ledSize / 2);
            }
            else {
                // LED AUS: Dunkles, mattes Rot.
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