package de.freese.simulationen.noise;

import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JComponent;

/**
 * @author Thomas Freese
 * @since 30.11.2024
 */
final class WhiteNoiseComponent extends AbstractWhiteNoise {
    private final JComponent component;

    WhiteNoiseComponent(final int pixelWidth, final int pixelHeight) {
        super(pixelWidth, pixelHeight);

        component = new JComponent() {
            @Override
            protected void paintChildren(final Graphics g) {
                // There are no Children.
                // super.paintChildren(g);
            }

            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);

                g.drawImage(getImage(), 0, 0, getWidth(), getHeight(), null);

                // Synchronizing the painting on systems that buffer graphics events.
                // Without this line, the animation might not be smooth on Linux.
                Toolkit.getDefaultToolkit().sync();
            }
        };
        // component.setDoubleBuffered(true);
    }

    JComponent getComponent() {
        return component;
    }

    @Override
    protected void repaintImage() {
        component.repaint();
    }
}
