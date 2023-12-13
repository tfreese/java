package de.freese.sonstiges.imap.bayes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/**
 * @author Thomas Freese
 */
public class NaiveBayesClassifier {

    private static final double DEFAULT_FACTOR = 1.0D;

    public double classify(final Collection<Merkmal> vector) {
        if (vector == null || vector.isEmpty()) {
            return 0.0D;
        }

        //        return classifyClassic(vector);
        //        return classifyLog(vector);
        return classifyBigDecimal(vector);
    }

    double classifyBigDecimal(final Collection<Merkmal> vector) {
        BigDecimal spamLikelihood = BigDecimal.valueOf(1.0D);
        BigDecimal hamLikelihood = BigDecimal.valueOf(1.0D);

        for (Merkmal merkmal : vector) {
            // Je nach Häufigkeit/Wichtigkeit des Wortes entsprechend dazu multiplizieren.
            for (int i = 0; i < merkmal.getWeight(); i++) {
                // Spam-Likelihood berechnen (Produkt der Log-Wahrscheinlichkeiten)
                spamLikelihood = spamLikelihood.multiply(BigDecimal.valueOf(merkmal.getSpamProbability()));

                // Ham-Likelihood berechnen (Produkt der Log-Wahrscheinlichkeiten)
                hamLikelihood = hamLikelihood.multiply(BigDecimal.valueOf(merkmal.getHamProbability()));
            }
        }

        // Spam Wahrscheinlichkeit berechnen
        BigDecimal spamProbability = spamLikelihood.divide(hamLikelihood.add(spamLikelihood), RoundingMode.HALF_UP);

        return spamProbability.doubleValue();
    }

    /**
     * Hier können sehr kleine oder sehr große Zahlen entstehen.
     */
    double classifyClassic(final Collection<Merkmal> vector) {
        double spamLikelihood = 1.0D;
        double hamLikelihood = 1.0D;

        for (Merkmal merkmal : vector) {
            // Je nach Häufigkeit/Wichtigkeit des Wortes entsprechend dazu multiplizieren.
            for (int i = 0; i < merkmal.getWeight(); i++) {
                // Spam-Likelihood berechnen (Produkt der Wahrscheinlichkeiten)
                spamLikelihood *= (merkmal.getSpamProbability() * DEFAULT_FACTOR);

                // Ham-Likelihood berechnen (Produkt der Wahrscheinlichkeiten)
                hamLikelihood *= (merkmal.getHamProbability() * DEFAULT_FACTOR);
            }
        }

        // Spam Wahrscheinlichkeit berechnen
        return spamLikelihood / (hamLikelihood + spamLikelihood);
    }

    /**
     * Hier können NaN oder INVALID Zahlen entstehen.
     */
    double classifyLog(final Collection<Merkmal> vector) {
        double spamLikelihood = 0.0D;
        double hamLikelihood = 0.0D;

        for (Merkmal merkmal : vector) {
            // Je nach Häufigkeit/Wichtigkeit des Wortes entsprechend dazu multiplizieren.
            for (int i = 0; i < merkmal.getWeight(); i++) {
                // Spam-Likelihood berechnen (Produkt der Log-Wahrscheinlichkeiten)
                spamLikelihood += Math.log(merkmal.getSpamProbability());

                // Ham-Likelihood berechnen (Produkt der Log-Wahrscheinlichkeiten)
                hamLikelihood += Math.log(merkmal.getHamProbability());
            }
        }

        spamLikelihood = Math.exp(Math.abs(spamLikelihood));
        hamLikelihood = Math.exp(Math.abs(hamLikelihood));

        // Spam Wahrscheinlichkeit berechnen
        return 1.0D - spamLikelihood / (hamLikelihood + spamLikelihood);
    }
}
