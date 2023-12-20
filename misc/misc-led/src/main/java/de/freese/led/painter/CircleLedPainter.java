// Created: 20.12.23
package de.freese.led.painter;

import java.awt.Graphics;

/**
 * @author Thomas Freese
 */
public class CircleLedPainter extends AbstractLedPainter {
    @Override
    public void paintLeds(final Graphics graphics, final int width, final int height) {
        graphics.setColor(getBackgroundColor());
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(getDotOffColor());

        for (int x = 0; x < width; x += getDotWidth()) {
            for (int y = 0; y < height; y += getDotHeight()) {
                graphics.fillOval(x, y, getDotWidth(), getDotHeight());
                y += getvGap();
            }

            x += gethGap();
        }
    }
}
