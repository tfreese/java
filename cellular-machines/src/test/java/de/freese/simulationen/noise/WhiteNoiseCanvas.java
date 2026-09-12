package de.freese.simulationen.noise;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Toolkit;

/**
 * @author Thomas Freese
 * @since 30.11.2024
 */
final class WhiteNoiseCanvas extends AbstractWhiteNoise {
    private final Canvas canvas;

    WhiteNoiseCanvas(final int pixelWidth, final int pixelHeight) {
        super(pixelWidth, pixelHeight);

        canvas = new Canvas() {
            @Override
            public void paint(final Graphics g) {
                g.drawImage(getImage(), 0, 0, getWidth(), getHeight(), null);

                // Synchronizing the painting on systems that buffer graphics events.
                // Without this line, the animation might not be smooth on Linux.
                Toolkit.getDefaultToolkit().sync();
            }
        };
    }

    Canvas getCanvas() {
        return canvas;
    }

    @Override
    protected void repaintImage() {
        canvas.repaint();
    }
}
