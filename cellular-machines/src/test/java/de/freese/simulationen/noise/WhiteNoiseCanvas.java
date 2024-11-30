// Created: 30 Nov. 2024
package de.freese.simulationen.noise;

import java.awt.Canvas;
import java.awt.Graphics;

/**
 * @author Thomas Freese
 */
final class WhiteNoiseCanvas extends AbstractWhiteNoise {
    private final Canvas canvas;

    WhiteNoiseCanvas(final int pixelWidth, final int pixelHeight) {
        super(pixelWidth, pixelHeight);

        canvas = new Canvas() {
            @Override
            public void paint(final Graphics g) {
                g.drawImage(getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        canvas.setBackground(null);
    }

    Canvas getCanvas() {
        return canvas;
    }

    @Override
    protected void repaintImage() {
        canvas.repaint();
    }
}
