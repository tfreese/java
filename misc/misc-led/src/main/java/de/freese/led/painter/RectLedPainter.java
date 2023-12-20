// Created: 20.12.23
package de.freese.led.painter;

import java.awt.Graphics;

/**
 * @author Thomas Freese
 */
public class RectLedPainter extends AbstractLedPainter {
    @Override
    public void paintLeds(final Graphics graphics, final int width, final int height) {
        graphics.setColor(getBackgroundColor());
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(getDotOffColor());

        // Horizontal Bars in DotOffColor.
        for (int y = 0; y < height; y += getDotHeight()) {
            graphics.fillRect(0, y, width, getDotHeight());
            y += getvGap();
        }

        // Vertical Gaps in Background Color.
        graphics.setColor(getBackgroundColor());

        for (int x = getDotWidth(); x < width; x += getDotWidth()) {
            graphics.fillRect(x, 0, gethGap(), height);
            x += gethGap();
        }
    }
}
