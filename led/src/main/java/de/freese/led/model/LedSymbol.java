package de.freese.led.model;

import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
public enum LedSymbol implements SymbolPainter {
    ARROW_UP {
        @Override
        public void paintSymbol(final Graphics2D g2d, final int width, final int height) {
            final int[] xPoints = {width / 2, 0, width}; // Oben (Mitte), Unten-Links, Unten-Rechts
            final int[] yPoints = {3, height - 5, height - 5}; // Oben, Unten-Links, Unten-Rechts (Y wird nach unten größer!)

            final Polygon triangle = new Polygon(xPoints, yPoints, 3);

            g2d.fill(triangle);
        }
    },
    ARROW_DOWN {
        @Override
        public void paintSymbol(final Graphics2D g2d, final int width, final int height) {
            final int[] xPoints = {0, width / 2, width}; // Oben (Mitte), Unten-Links, Unten-Rechts
            final int[] yPoints = {5, height - 5, 5}; // Oben, Unten-Links, Unten-Rechts (Y wird nach unten größer!)

            final Polygon triangle = new Polygon(xPoints, yPoints, 3);

            g2d.fill(triangle);
        }
    }
}
