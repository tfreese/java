// Created: 20.12.23
package de.freese.led.model.token;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public interface LedToken {
    default Color getColor() {
        return null;
    }

    String getValue();
}
