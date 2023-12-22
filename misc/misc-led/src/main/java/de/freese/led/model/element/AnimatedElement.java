// Created: 22.12.23
package de.freese.led.model.element;

import java.util.ArrayList;
import java.util.List;

import de.freese.led.model.token.LedToken;
import de.freese.led.model.token.TextToken;

/**
 * @author Thomas Freese
 */
public class AnimatedElement implements LedElement {

    private final List<LedToken> tokens = new ArrayList<>();

    public AnimatedElement() {
        super();
    }

    public AnimatedElement(final String value) {
        super();

        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);

            addToken(new TextToken(String.valueOf(c)));
        }
    }

    public AnimatedElement addToken(final LedToken token) {
        tokens.add(token);

        return this;
    }

    @Override
    public LedToken[] getTokens() {
        return tokens.toArray(new LedToken[0]);
    }

    public void shiftToken() {
        if (tokens.isEmpty()) {
            return;
        }

        final LedToken token = tokens.removeFirst();
        tokens.add(token);
    }
}
