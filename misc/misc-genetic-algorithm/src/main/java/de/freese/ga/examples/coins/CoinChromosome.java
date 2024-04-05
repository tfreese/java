// Created: 29.06.2020
package de.freese.ga.examples.coins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.freese.ga.Chromosome;
import de.freese.ga.Gene;

/**
 * @author Thomas Freese
 */
public class CoinChromosome extends Chromosome {
    public CoinChromosome(final CoinConfig config) {
        super(config);
    }

    @Override
    public double calcFitnessValue() {
        // Münzinhalt des Chromosoms in Cent.
        final int cents = Stream.of(getGenes()).mapToInt(Gene::getInteger).sum();

        final int targetCent = getConfig().getTargetCents();

        final int changeDifference = Math.abs(targetCent - cents);

        // 99 Cent ist maximum.
        double fitness = getConfig().getMaximumCents() - changeDifference;

        // Zielbetrag erreicht.
        if (cents == targetCent) {
            // fitness += 100 - (10 * totalCoins);
            fitness = getConfig().getMaxFitness();
        }

        return fitness;
    }

    @Override
    public void populate() {
        final List<Integer> existingCoins = getConfig().getExistingCoins();

        final List<Gene> genes = new ArrayList<>();

        for (int i = 0; i < size(); i++) {
            genes.add(Gene.of(existingCoins.get(i)));
        }

        // Zufällig neu zusammenstellen.
        Collections.shuffle(genes);

        for (int i = 0; i < size(); i++) {
            setGene(i, genes.get(i));
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Stream.of(getGenes())
                 .map(Gene::getInteger)
                 .filter(coin -> coin > 0)
                 .map(Object::toString)
                 .collect(Collectors.joining(" + "));
        // @formatter:on
    }

    @Override
    protected CoinConfig getConfig() {
        return (CoinConfig) super.getConfig();
    }
}
