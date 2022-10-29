package de.freese.sonstiges.imap.bayes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Merkmal für einen Classifier.
 *
 * @author Thomas Freese
 */
public class Merkmal implements Serializable
{
    /**
     *
     */
    public static final Double DEFAULT_HAM_PROBABILITY = 0.001D;
    /**
     *
     */
    public static final Double DEFAULT_SPAM_PROBABILITY = 0.001D;
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5899758231801834734L;
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
    private final String token;
    /**
     *
     */
    private final int weight;

    /**
     * Creates a new Merkmal object.
     *
     * @param token String
     * @param hamCount int
     * @param spamCount int
     */
    public Merkmal(String token, int hamCount, int spamCount)
    {
        this(token, hamCount, spamCount, 1);
    }

    /**
     * Creates a new Merkmal object.
     *
     * @param token String
     * @param hamCount int
     * @param spamCount int
     * @param weight int
     */
    public Merkmal(String token, int hamCount, int spamCount, int weight)
    {
        this.token = Objects.requireNonNull(token, "token required");
        this.hamCount = hamCount;
        this.spamCount = spamCount;
        this.weight = weight;
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

        Merkmal merkmal = (Merkmal) o;

        return hamCount == merkmal.hamCount && spamCount == merkmal.spamCount && weight == merkmal.weight && token.equals(merkmal.token);
    }

    /**
     * @return int
     */
    public int getCount()
    {
        return getHamCount() + getSpamCount();
    }

    /**
     * @return int
     */
    public int getHamCount()
    {
        return this.hamCount;
    }

    /**
     * @return double
     */
    public double getHamProbability()
    {
        if (getHamCount() == 0)
        {
            return DEFAULT_HAM_PROBABILITY;
        }

        return (double) getHamCount() / getCount();
    }

    /**
     * @return int
     */
    public int getSpamCount()
    {
        return this.spamCount;
    }

    /**
     * @return double
     */
    public double getSpamProbability()
    {
        if (getSpamCount() == 0)
        {
            return DEFAULT_SPAM_PROBABILITY;
        }

        return (double) getSpamCount() / getCount();
    }

    /**
     * @return String
     */
    public String getToken()
    {
        return token;
    }

    /**
     * @return int
     */
    public int getWeight()
    {
        return weight;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hamCount, spamCount, token, weight);
    }
}
