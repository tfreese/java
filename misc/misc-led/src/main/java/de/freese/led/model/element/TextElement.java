// Created: 20.12.23
package de.freese.led.model.element;

import java.awt.Color;

import de.freese.led.model.token.LedToken;
import de.freese.led.model.token.TextToken;

/**
 * @author Thomas Freese
 */
public class TextElement extends AbstractLedElement {

    private final LedToken[] tokens;

    public TextElement() {
        this(null, null);
    }

    public TextElement(final String value) {
        this(value, null);
    }

    public TextElement(final String value, final Color color) {
        super(color);

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
