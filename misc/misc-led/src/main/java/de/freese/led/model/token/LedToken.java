package de.freese.led.model.token;

import java.awt.Color;

/**
 * @author Thomas Freese
 * @since 20.12.23
 */
@FunctionalInterface
public interface LedToken {
    default Color getColor() {
        return Color.WHITE;
    }

    String getValue();
}
