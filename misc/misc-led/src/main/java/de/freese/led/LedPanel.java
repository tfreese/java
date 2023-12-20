// Created: 20.12.23
package de.freese.led;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.freese.led.painter.AbstractLedPainter;
import de.freese.led.painter.CircleLedPainter;
import de.freese.led.painter.RectLedPainter;

/**
 * @author Thomas Freese
 */
public class LedPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 9187009021303433483L;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame();
            //        frame.pack();
            frame.setSize(2000, 200);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            final JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1));
            panel.add(new LedPanel(new RectLedPainter()));
            panel.add(new LedPanel(new CircleLedPainter()));

            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }

    private final transient AbstractLedPainter ledPainter;

    public LedPanel(final AbstractLedPainter ledPainter) {
        super();

        this.ledPainter = ledPainter;

        setBackground(null);
        setLayout(null);
        setDoubleBuffered(true);
    }

    //    @Override
    //    public void paint(final Graphics g) {
    //        super.paint(g);
    //    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        this.ledPainter.paintLeds(g, getWidth(), getHeight());
    }
}
