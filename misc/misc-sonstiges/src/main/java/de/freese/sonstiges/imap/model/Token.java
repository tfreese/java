package de.freese.sonstiges.imap.model;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class Token {

    private final int hamCount;
    private final int spamCount;
    private final String value;

    public Token(final String value, final int hamCount, final int spamCount) {
        super();

        this.value = value;
        this.hamCount = hamCount;
        this.spamCount = spamCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Token token = (Token) o;

        return hamCount == token.hamCount && spamCount == token.spamCount && value.equals(token.value);
    }

    public int getHamCount() {
        return this.hamCount;
    }

    public int getSpamCount() {
        return this.spamCount;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hamCount, spamCount, value);
    }
}
