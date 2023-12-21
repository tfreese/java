// Created: 20.12.23
package de.freese.led.painter;

import java.awt.Graphics;

import de.freese.led.encoder.SymbolEncoder;

/**
 * @author Thomas Freese
 */
public class CircleLedPainter extends AbstractLedPainter {
    public CircleLedPainter(final SymbolEncoder symbolEncoder) {
        super(symbolEncoder);
    }

    @Override
    public void paintOfflineDots(final Graphics graphics, final int width, final int height) {
        graphics.setColor(getBackgroundColor());
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(getDotOfflineColor());

        for (int x = 0; x < width; x += getDotWidth() + gethGap()) {
            for (int y = 0; y < height; y += getDotHeight() + getvGap()) {
                paintDot(graphics, x, y, getDotWidth(), getDotHeight());
            }
        }
    }

    @Override
    protected void paintDot(final Graphics graphics, final int x, final int y, final int width, final int height) {
        graphics.fillOval(x, y, width, height);
    }
}
