// Created: 20.12.23
package de.freese.led.model.element;

import de.freese.led.model.token.LedToken;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface LedElement {
    LedToken[] getTokens();
}
