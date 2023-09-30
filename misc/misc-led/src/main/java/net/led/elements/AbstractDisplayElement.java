package net.led.elements;

import net.led.tokens.Token;

/**
 *
 */
public abstract class AbstractDisplayElement implements Element {
    private final Token[] tokens;

    protected AbstractDisplayElement(final Token[] tokens) {
        super();

        if (tokens == null) {
            throw new IllegalArgumentException("tokens array is null");
        }

        this.tokens = tokens;
    }

    @Override
    public Token[] getTokens() {
        return this.tokens;
    }
}
