// Created: 20.12.23
package de.freese.led.model.element;

import de.freese.led.model.token.LedToken;
import de.freese.led.model.token.TextToken;

/**
 * @author Thomas Freese
 */
public class TextElement implements LedElement {

    private final LedToken[] tokens;

    public TextElement() {
        this(null);
    }

    public TextElement(final String value) {
        super();

        tokens = new LedToken[]{new TextToken(value)};
    }

    @Override
    public LedToken[] getTokens() {
        return tokens;
    }

    public void setValue(final String value) {
        ((TextToken) tokens[0]).setValue(value);
    }
}
