// Created: 20.12.23
package de.freese.led.model.element;

import java.awt.Color;

import de.freese.led.model.token.LedToken;

/**
 * @author Thomas Freese
 */
public interface LedElement {
    default Color getColor() {
        return null;
    }

    LedToken[] getTokens();
}
