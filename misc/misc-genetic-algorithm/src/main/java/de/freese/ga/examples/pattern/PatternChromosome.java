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

    @Override
    public double calcFitnessValue() {
        double fitness = 0.0D;

        final boolean[] solution = getConfig().getSolution();

        for (int i = 0; i < size() && i < solution.length; i++) {
            if (getGene(i).getValue().equals(solution[i])) {
                fitness++;
            }
        }

        return fitness;
    }

    @Override
    public void populate() {
        // Zufällige Initialisierung.
        for (int i = 0; i < size(); i++) {
            setGene(i, Gene.of(getConfig().getRandom().nextBoolean()));
        }
    }

    @Override
    public String toString() {
        return Stream.of(getGenes())
                .map(Gene::getValue)
                .map(v -> Boolean.TRUE.equals(v) ? '1' : '0')
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    @Override
    protected PatternConfig getConfig() {
        return (PatternConfig) super.getConfig();
    }
}
