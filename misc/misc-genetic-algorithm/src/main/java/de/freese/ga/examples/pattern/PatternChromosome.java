// Created: 29.06.2020
package de.freese.ga.examples.pattern;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.freese.ga.Chromosome;
import de.freese.ga.Gene;

/**
 * @author Thomas Freese
 */
public class PatternChromosome extends Chromosome {
    public PatternChromosome(final PatternConfig config) {
        super(config);
    }

    /**
     * @see de.freese.ga.Chromosome#calcFitnessValue()
     */
    @Override
    public double calcFitnessValue() {
        double fitness = 0.0D;

        boolean[] solution = getConfig().getSolution();

        for (int i = 0; (i < size()) && (i < solution.length); i++) {
            if (getGene(i).getValue().equals(solution[i])) {
                fitness++;
            }
        }

        return fitness;
    }

    /**
     * @see de.freese.ga.Chromosome#populate()
     */
    @Override
    public void populate() {
        // Zufällige Initialisierung.
        for (int i = 0; i < size(); i++) {
            setGene(i, new Gene(getConfig().getRandom().nextBoolean()));
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // @formatter:off
        return Stream.of(getGenes())
                 .map(Gene::getValue)
                 .map(v -> Boolean.TRUE.equals(v) ? '1': '0')
                 .map(Object::toString)
                 .collect(Collectors.joining());
        // @formatter:on
    }

    /**
     * @see de.freese.ga.Chromosome#getConfig()
     */
    @Override
    protected PatternConfig getConfig() {
        return (PatternConfig) super.getConfig();
    }
}
