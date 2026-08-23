package de.freese.led.model.element;

import de.freese.led.model.token.LedToken;

/**
 * @author Thomas Freese
 * @since 20.12.23
 */
@FunctionalInterface
public interface LedElement {
    LedToken[] getTokens();
}
