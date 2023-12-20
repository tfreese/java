// Created: 20.12.23
package de.freese.led.model.element;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLedElement implements LedElement {
    private Color color;

    protected AbstractLedElement() {
        super();
    }

    protected AbstractLedElement(final Color color) {
        super();

        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }
}
