// Created: 20.12.23
package de.freese.led.model.token;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLedToken implements LedToken {
    private Color color;

    protected AbstractLedToken() {
        super();
    }

    protected AbstractLedToken(final Color color) {
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
