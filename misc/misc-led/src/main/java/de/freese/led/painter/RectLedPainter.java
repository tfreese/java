// Created: 20.12.23
package de.freese.led.painter;

import java.awt.Graphics;

import de.freese.led.encoder.SymbolEncoder;

/**
 * @author Thomas Freese
 */
public class RectLedPainter extends AbstractLedPainter {
    public RectLedPainter(final SymbolEncoder symbolEncoder) {
        super(symbolEncoder);
    }

    @Override
    public void paintOfflineDots(final Graphics graphics, final int width, final int height) {
        graphics.setColor(getBackgroundColor());
        graphics.fillRect(0, 0, width, height);

        // Horizontal Bars in DotOfflineColor.
        graphics.setColor(getDotOfflineColor());

        int yOffset = 0;

        for (int y = 0; y < height; y += getDotHeight()) {
            paintDot(graphics, 0, y + yOffset, width, getDotHeight());
            yOffset += getvGap();
        }

        // Vertical Gaps in Background Color.
        graphics.setColor(getBackgroundColor());

        int xOffset = 0;

        for (int x = getDotWidth(); x < width; x += getDotWidth()) {
            paintDot(graphics, x + xOffset, 0, gethGap(), height);
            xOffset += gethGap();
        }
    }

    @Override
    protected void paintDot(final Graphics graphics, final int x, final int y, final int width, final int height) {
        graphics.fillRect(x, y, width, height);
    }
}
