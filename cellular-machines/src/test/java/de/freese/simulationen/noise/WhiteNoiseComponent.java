// Created: 30 Nov. 2024
package de.freese.simulationen.noise;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * @author Thomas Freese
 */
final class WhiteNoiseComponent extends AbstractWhiteNoise {
    private final JComponent component;

    WhiteNoiseComponent(final int pixelWidth, final int pixelHeight) {
        super(pixelWidth, pixelHeight);

        component = new JComponent() {
            @Override
            public void paint(final Graphics g) {
                g.drawImage(getImage(), 0, 0, getWidth(), getHeight(), null);
            }

            @Override
            protected void paintChildren(final Graphics g) {
                // There are no Children.
                // super.paintChildren(g);
            }

            @Override
            protected void paintComponent(final Graphics g) {
                // Ignore
                // super.paintComponent(g);
            }
        };
        component.setDoubleBuffered(true);
        component.setBackground(null);
        component.setLayout(null);
    }

    public JComponent getComponent() {
        return component;
    }

    @Override
    protected void repaintImage() {
        component.repaint();
    }
}
