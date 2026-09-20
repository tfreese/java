package de.freese.led.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
public final class LedModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -2751656423937836829L;
    private final int displayedColumns;
    private final int displayedRows;
    private final Font font;
    private final transient List<LedColumn> ledColumns = new ArrayList<>();

    /**
     * new Color(255, 30, 30)
     */
    private Color colorActive = Color.LIGHT_GRAY;
    private Color colorBackground = Color.BLACK;
    /**
     * new Color(40, 10, 10)
     */
    private Color colorInactive = Color.DARK_GRAY;
    private int ledGap = 2;
    private int ledSize = 8;

    public LedModel(final int displayedRows, final int displayedColumns) {
        this(displayedRows, displayedColumns, new Font(Font.MONOSPACED, Font.PLAIN, displayedRows - 4));
    }

    public LedModel(final int displayedRows, final int displayedColumns, final Font font) {
        super();

        this.displayedRows = displayedRows;
        this.displayedColumns = displayedColumns;
        this.font = Objects.requireNonNull(font, "font required");
    }

    public LedModel addColumn(final boolean[] leds) {
        return addColumn(leds, colorActive);
    }

    public LedModel addColumn(final boolean[] leds, final Color color) {
        Objects.requireNonNull(leds, "leds required");
        Objects.requireNonNull(color, "color required");

        if (color.equals(colorActive)) {
            ledColumns.add(new LedColumn(leds));
        }
        else {
            ledColumns.add(new LedColumn(leds, color));
        }

        return this;
    }

    public LedModel addSymbol(final SymbolPainter symbolPainter, final Color color) {
        Objects.requireNonNull(symbolPainter, "symbolPainter required");

        final boolean[][] matrix = convertSymbolToMatrix(symbolPainter);

        for (final boolean[] column : matrix) {
            addColumn(column, color);
        }

        return this;
    }

    public LedModel addText(final String text) {
        return addText(text, colorActive);
    }

    public LedModel addText(final String text, final Color color) {
        Objects.requireNonNull(text, "text required");
        Objects.requireNonNull(color, "color required");

        final boolean[][] matrix = convertTextToMatrix(text, font);

        for (final boolean[] column : matrix) {
            addColumn(column, color);
        }

        return this;
    }

    public int columnCount() {
        return ledColumns.size();
    }

    public int displayedColumns() {
        return displayedColumns;
    }

    public int displayedRows() {
        return displayedRows;
    }

    public Color getColorActive(final int column) {
        final Color color = ledColumns.get(column).getColorActive();

        return (color != null) ? color : colorActive;
    }

    public Color getColorActive() {
        return colorActive;
    }

    public Color getColorBackground() {
        return colorBackground;
    }

    public Color getColorInactive() {
        return colorInactive;
    }

    public int getColumnWidth() {
        return ledSize + ledGap;
    }

    public int getLedGap() {
        return ledGap;
    }

    public int getLedSize() {
        return ledSize;
    }

    public boolean isActive(final int row, final int column) {
        return ledColumns.get(column).isActive(row);
    }

    public LedModel setColorActive(final Color colorActive) {
        this.colorActive = Objects.requireNonNull(colorActive, "colorActive required");

        return this;
    }

    public LedModel setColorBackground(final Color colorBackground) {
        this.colorBackground = Objects.requireNonNull(colorBackground, "colorBackground required");

        return this;
    }

    public LedModel setColorInactive(final Color colorInactive) {
        this.colorInactive = Objects.requireNonNull(colorInactive, "colorInactive required");

        return this;
    }

    public LedModel setLedGap(final int ledGap) {
        this.ledGap = ledGap;

        return this;
    }

    public LedModel setLedSize(final int ledSize) {
        this.ledSize = ledSize;

        return this;
    }

    private boolean[][] convertImageToMatrix(final BufferedImage image) {
        final boolean[][] ledMatrix = new boolean[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // Pixel auslesen (ARGB-Wert).
                final int argb = image.getRGB(x, y);

                // Wenn der Pixel nicht transparent ist (Alpha > 0), ist die LED "an".
                final int alpha = (argb >> 24) & 0xFF;

                // Schwellenwert für knackige Kanten.
                ledMatrix[x][y] = alpha > 128;
            }
        }

        return ledMatrix;
    }

    private boolean[][] convertSymbolToMatrix(final SymbolPainter symbolPainter) {
        final BufferedImage image = new BufferedImage(20, displayedRows, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setColor(Color.BLACK);

        symbolPainter.paintSymbol(g2d, image.getWidth(), image.getHeight());

        g2d.dispose();

        // Bild in ein boolean-Raster übersetzen.
        return convertImageToMatrix(image);
    }

    private boolean[][] convertTextToMatrix(final String text, final Font font) {
        // 1. Breite des Textes berechnen, um das Bild exakt zu dimensionieren.
        final BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImg.createGraphics();
        g2d.setFont(font);

        final FontMetrics fm = g2d.getFontMetrics();
        final int matrixWidth = fm.stringWidth(text);
        g2d.dispose();

        // 2. Das eigentliche Bild in der passenden Größe erstellen (ohne Kantenglättung).
        final BufferedImage ledImage = new BufferedImage(matrixWidth, displayedRows, BufferedImage.TYPE_INT_ARGB);
        g2d = ledImage.createGraphics();
        g2d.setFont(font);

        // WICHTIG: Antialiasing abschalten für klare, harte "LED-Kanten".
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // Textfarbe für den Kontrast.
        g2d.setColor(Color.BLACK);

        // Text auf der Baseline zeichnen (fm.getAscent sorgt für korrekte vertikale Ausrichtung).
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        // 3. Bild in ein boolean-Raster übersetzen.
        return convertImageToMatrix(ledImage);
    }
}
