package de.freese.led.model;

import java.awt.Graphics2D;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
@FunctionalInterface
public interface SymbolPainter {
    void paintSymbol(Graphics2D g2d, int width, int height);
}
