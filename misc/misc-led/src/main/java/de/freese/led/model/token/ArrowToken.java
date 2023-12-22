// Created: 22.12.23
package de.freese.led.model.token;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public class ArrowToken extends AbstractLedToken {
    public enum Arrow {
        DECREASING,
        INCREASING,
        LEFT,
        RIGHT,
        UNCHANGED
    }

    private Arrow arrow;

    public ArrowToken() {
        super();
    }

    public ArrowToken(final Arrow arrow) {
        this(arrow, null);
    }

    public ArrowToken(final Arrow arrow, final Color color) {
        super(color);

        this.arrow = arrow;
    }

    public Arrow getArrow() {
        return arrow;
    }

    @Override
    public String getValue() {
        return "";
    }

    public void setValue(final Arrow arrow) {
        this.arrow = arrow;
    }
}
