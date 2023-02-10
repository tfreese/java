// Created: 29.06.2020
package de.freese.ga.examples.coins;

import java.util.Objects;
import java.util.stream.Stream;

import de.freese.ga.Chromosome;
import de.freese.ga.Gene;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public class CoinGenotype extends Genotype {
    public CoinGenotype(final CoinConfig config) {
        super(config);
    }

    private CoinGenotype(final CoinConfig config, final int size) {
        super(config, size);
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyChromosome()
     */
    @Override
    public Chromosome createEmptyChromosome() {
        return new CoinChromosome(getConfig());
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyGenotype(int)
     */
    @Override
    public Genotype createEmptyGenotype(final int size) {
        return new CoinGenotype(getConfig(), size);
    }

    /**
     * @see de.freese.ga.Genotype#crossover(de.freese.ga.Chromosome, de.freese.ga.Chromosome)
     */
    @Override
    public Chromosome crossover(final Chromosome parent1, final Chromosome parent2) {
        Chromosome population = createEmptyChromosome();

        for (int i = 0; i < parent1.size(); i++) {
            final Gene coin;

            if (getConfig().getRandom().nextDouble() <= getConfig().getCrossoverRate()) {
                coin = parent1.getGene(i);
            }
            else {
                coin = parent2.getGene(i);
            }

            // Zählen wie viele Münzen von diesem Wert insgesamt vorhanden sind.
            long coinsExisting = getConfig().getCoinCounter().getOrDefault(coin.getValue(), 1L);

            // Zählen wie viele Münzen von diesem Wert im Chromosom bereits vorhanden sind.
            long coinsInPopulation = Stream.of(population.getGenes()).filter(Objects::nonNull).filter(g -> g.getValue().equals(coin.getValue())).count();

            // Münze eines Wertes nur zuweisen, wenn noch welche übrig sind.
            if (coinsInPopulation < coinsExisting) {
                population.setGene(i, coin);
            }
            else {
                population.setGene(i, new Gene(0));
            }
        }

        return population;
    }

    /**
     * @see de.freese.ga.Genotype#getConfig()
     */
    @Override
    protected CoinConfig getConfig() {
        return (CoinConfig) super.getConfig();
    }
}
