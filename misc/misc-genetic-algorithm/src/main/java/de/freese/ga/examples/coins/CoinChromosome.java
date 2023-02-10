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

    /**
     * @see de.freese.ga.Chromosome#calcFitnessValue()
     */
    @Override
    public double calcFitnessValue() {
        // Münzinhalt des Chromosoms in Cent.
        int cents = Stream.of(getGenes()).mapToInt(Gene::getInteger).sum();

        int targetCent = getConfig().getTargetCents();

        int changeDifference = Math.abs(targetCent - cents);

        // 99 Cent ist maximum.
        double fitness = (getConfig().getMaximumCents() - changeDifference);

        // Zielbetrag erreicht.
        if (cents == targetCent) {
            // fitness += 100 - (10 * totalCoins);
            fitness = getConfig().getMaxFitness();
        }

        return fitness;
    }

    /**
     * @see de.freese.ga.Chromosome#populate()
     */
    @Override
    public void populate() {
        List<Integer> existingCoins = getConfig().getExistingCoins();

        List<Gene> genes = new ArrayList<>();

        for (int i = 0; i < size(); i++) {
            genes.add(new Gene(existingCoins.get(i)));
        }

        // Zufällig neu zusammenstellen.
        Collections.shuffle(genes);

        for (int i = 0; i < size(); i++) {
            setGene(i, genes.get(i));
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
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

    /**
     * @see de.freese.ga.Chromosome#getConfig()
     */
    @Override
    protected CoinConfig getConfig() {
        return (CoinConfig) super.getConfig();
    }
}
