// Created: 20.12.23
package de.freese.led.painter;

import java.awt.Color;
import java.awt.Graphics;

import de.freese.led.model.element.LedElement;
import de.freese.led.model.token.LedToken;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLedPainter {
    private Color backgroundColor = new Color(17, 17, 17);
    private int dotHeight = 50;
    private Color dotOffColor = new Color(102, 102, 102);
    private int dotWidth = 50;
    private int hGap = 10;
    private int vGap = 10;

    protected AbstractLedPainter() {
        super();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getDotHeight() {
        return dotHeight;
    }

    public Color getDotOffColor() {
        return dotOffColor;
    }

    public int getDotWidth() {
        return dotWidth;
    }

    public int gethGap() {
        return hGap;
    }

    public int getvGap() {
        return vGap;
    }

    public void paint(final Graphics graphics, final LedElement ledElement, final int width, final int height) {
        // TODO
    }

    public void paint(final Graphics graphics, final LedToken ledToken, final int x, final int offset) {
        // TODO
    }

    public abstract void paintLeds(Graphics graphics, int width, int height);

    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setDotHeight(final int dotHeight) {
        this.dotHeight = dotHeight;
    }

    public void setDotOffColor(final Color dotOffColor) {
        this.dotOffColor = dotOffColor;
    }

    public void setDotWidth(final int dotWidth) {
        this.dotWidth = dotWidth;
    }

    public void sethGap(final int hGap) {
        this.hGap = hGap;
    }

    public void setvGap(final int vGap) {
        this.vGap = vGap;
    }
}
