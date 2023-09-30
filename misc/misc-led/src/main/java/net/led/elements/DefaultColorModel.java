package net.led.elements;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public class DefaultColorModel implements ColorModel {
    private Color color;

    public DefaultColorModel() {
        this(Color.white);
    }

    public DefaultColorModel(final Color color) {
        super();

        this.color = color;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(final Color color) {
        this.color = color;
    }
}
