package de.freese.led2;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
public final class LedFontConverter {
    /**
     * Konvertiert Text in ein 2D-LED-Raster.
     *
     * @param text Der zu konvertierende Text.
     * @param matrixHeight Die feste Höhe des LED-Rasters (z.B. 8 für eine 8-Bit Matrix).
     * @param font Die gewünschte Schriftart (z.B. Monospaced, 8pt oder 16pt).
     *
     * @return Ein 2D-Boolean-Array [Spalte][Zeile] (true = LED an).
     */
    public static boolean[][] convertTextToLED(final String text, final int matrixHeight, final Font font) {
        Objects.requireNonNull(text, "text required");
        Objects.requireNonNull(font, "font required");

        // 1. Breite des Textes berechnen, um das Bild exakt zu dimensionieren.
        final BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImg.createGraphics();
        g2d.setFont(font);

        final FontMetrics fm = g2d.getFontMetrics();
        final int matrixWidth = fm.stringWidth(text);
        g2d.dispose();

        // 2. Das eigentliche Bild in der passenden Größe erstellen (ohne Kantenglättung).
        final BufferedImage ledImage = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_ARGB);
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
        final boolean[][] ledMatrix = new boolean[matrixWidth][matrixHeight];

        for (int x = 0; x < matrixWidth; x++) {
            for (int y = 0; y < matrixHeight; y++) {
                // Pixel auslesen (ARGB-Wert).
                final int argb = ledImage.getRGB(x, y);

                // Wenn der Pixel nicht transparent ist (Alpha > 0), ist die LED "an".
                final int alpha = (argb >> 24) & 0xff;

                // Schwellenwert für knackige Kanten.
                ledMatrix[x][y] = (alpha > 128);
            }
        }

        return ledMatrix;
    }

    private LedFontConverter() {
        super();
    }
}
