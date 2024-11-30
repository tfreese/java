// Created: 20.12.23
package de.freese.led;

import java.awt.Graphics;
import java.io.Serial;

import javax.swing.JComponent;

import de.freese.led.model.element.LedElement;
import de.freese.led.painter.LedPainter;

/**
 * @author Thomas Freese
 */
class LedPanel extends JComponent {
    @Serial
    private static final long serialVersionUID = 9187009021303433483L;

    static LedPanel of(final LedPainter ledPainter) {
        final LedPanel ledPanel = new LedPanel(ledPainter);
        ledPanel.setBackground(null);
        ledPanel.setLayout(null);
        ledPanel.setDoubleBuffered(true);

        return ledPanel;
    }

    private final transient LedPainter ledPainter;
    private transient LedElement ledElement;

    private LedPanel(final LedPainter ledPainter) {
        super();

        this.ledPainter = ledPainter;
    }

    // @Override
    // public void paint(final Graphics g) {
    //     super.paint(g);
    //
    //     ledPainter.paintOfflineDots(g, getWidth(), getHeight());
    //
    //     if (ledElement != null) {
    //         ledPainter.paintElement(g, ledElement, getWidth(), getHeight());
    //     }
    // }

    public void setLedElement(final LedElement ledElement) {
        this.ledElement = ledElement;
    }

    @Override
    protected void paintChildren(final Graphics g) {
        // There are no Children.
        // super.paintChildren(g);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        // super.paintComponent(g);

        ledPainter.paintOfflineDots(g, getWidth(), getHeight());

        if (ledElement != null) {
            ledPainter.paintElement(g, ledElement, getWidth(), getHeight());
        }
    }
}
