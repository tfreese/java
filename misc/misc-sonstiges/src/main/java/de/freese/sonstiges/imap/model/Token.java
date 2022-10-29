package de.freese.sonstiges.imap.model;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class Token
{
    /**
     *
     */
    private final int hamCount;
    /**
     *
     */
    private final int spamCount;
    /**
     *
     */
    private final String value;

    /**
     * @param value String
     * @param hamCount int
     * @param spamCount int
     */
    public Token(final String value, final int hamCount, final int spamCount)
    {
        this.value = value;
        this.hamCount = hamCount;
        this.spamCount = spamCount;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Token token = (Token) o;

        return hamCount == token.hamCount && spamCount == token.spamCount && value.equals(token.value);
    }

    /**
     * @return int
     */
    public int getHamCount()
    {
        return this.hamCount;
    }

    /**
     * @return int
     */
    public int getSpamCount()
    {
        return this.spamCount;
    }

    /**
     * @return java.lang.String
     */
    public String getValue()
    {
        return this.value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hamCount, spamCount, value);
    }
}
