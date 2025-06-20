package de.freese.sonstiges.imap.bayes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Characteristic for a {@link NaiveBayesClassifier}.
 *
 * @author Thomas Freese
 */
public class Merkmal implements Serializable {

    public static final Double DEFAULT_HAM_PROBABILITY = 0.001D;
    public static final Double DEFAULT_SPAM_PROBABILITY = 0.001D;

    @Serial
    private static final long serialVersionUID = -5899758231801834734L;

    private final int hamCount;
    private final int spamCount;
    private final String token;
    private final int weight;

    public Merkmal(final String token, final int hamCount, final int spamCount) {
        this(token, hamCount, spamCount, 1);
    }

    public Merkmal(final String token, final int hamCount, final int spamCount, final int weight) {
        super();

        this.token = Objects.requireNonNull(token, "token required");
        this.hamCount = hamCount;
        this.spamCount = spamCount;
        this.weight = weight;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Merkmal merkmal = (Merkmal) o;

        return hamCount == merkmal.hamCount && spamCount == merkmal.spamCount && weight == merkmal.weight && token.equals(merkmal.token);
    }

    public int getCount() {
        return getHamCount() + getSpamCount();
    }

    public int getHamCount() {
        return hamCount;
    }

    public double getHamProbability() {
        if (getHamCount() == 0) {
            return DEFAULT_HAM_PROBABILITY;
        }

        return (double) getHamCount() / getCount();
    }

    public int getSpamCount() {
        return spamCount;
    }

    public double getSpamProbability() {
        if (getSpamCount() == 0) {
            return DEFAULT_SPAM_PROBABILITY;
        }

        return (double) getSpamCount() / getCount();
    }

    public String getToken() {
        return token;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hamCount, spamCount, token, weight);
    }
}
