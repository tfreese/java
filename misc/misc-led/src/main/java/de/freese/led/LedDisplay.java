// Created: 21.12.23
package de.freese.led;

import java.awt.Color;
import java.util.Objects;

import javax.swing.JComponent;

import de.freese.led.encoder.SymbolEncoderFiveSeven;
import de.freese.led.model.element.LedElement;
import de.freese.led.painter.CircleLedPainter;
import de.freese.led.painter.LedPainter;
import de.freese.led.painter.RectLedPainter;

/**
 * @author Thomas Freese
 */
public final class LedDisplay {
    /**
     * Display for a 5 x 7 LED-Matrix with Circles.<br>
     */
    public static LedDisplay withCircles() {
        return new LedDisplay(new CircleLedPainter(new SymbolEncoderFiveSeven()));
    }

    /**
     * Display for a 5 x 7 LED-Matrix with Rectangles.<br>
     */
    public static LedDisplay withRectangles() {
        return new LedDisplay(new RectLedPainter(new SymbolEncoderFiveSeven()));
    }

    private final LedPainter ledPainter;
    private final LedPanel ledPanel;

    private LedDisplay(final LedPainter ledPainter) {
        super();

        this.ledPainter = Objects.requireNonNull(ledPainter, "ledPainter required");

        this.ledPanel = new LedPanel(ledPainter);
        this.ledPanel.setBackground(null);
        this.ledPanel.setLayout(null);
        this.ledPanel.setDoubleBuffered(true);
    }

    public JComponent getComponent() {
        return ledPanel;
    }

    public void setBackgroundColor(final Color backgroundColor) {
        ledPainter.setBackgroundColor(backgroundColor);
    }

    public void setDotHeight(final int dotHeight) {
        ledPainter.setDotHeight(dotHeight);
    }

    public void setDotOfflineColor(final Color dotOfflineColor) {
        ledPainter.setDotOfflineColor(dotOfflineColor);
    }

    public void setDotWidth(final int dotWidth) {
        ledPainter.setDotWidth(dotWidth);
    }

    public void setLedElement(final LedElement ledElement) {
        ledPanel.setLedElement(ledElement);
    }

    public void sethGap(final int hGap) {
        ledPainter.sethGap(hGap);
    }

    public void setvGap(final int vGap) {
        ledPainter.setvGap(vGap);
    }

    public void update() {
        ledPanel.repaint();
    }
}
