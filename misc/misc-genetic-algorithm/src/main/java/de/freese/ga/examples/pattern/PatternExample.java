// Created: 29.06.2020
package de.freese.ga.examples.pattern;

import de.freese.ga.Chromosome;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public class PatternExample
{
    public static void main(final String[] args)
    {
        String pattern = "11110000000000100000000001000000000010000000000100000000001111";

        PatternConfig config = new PatternConfig();
        // config.setElitism(false);
        config.setSizeGenotype(50); // Anzahl Chromosomen/Lösungen/Pattern
        config.setPattern(pattern);

        // Create an initial population
        Genotype population = new PatternGenotype(config);
        population.populate();

        Chromosome fittest = population.getFittest();

        // for (int i = 0; i < config.getSizeGenotype(); i++)
        for (int i = 0; fittest.calcFitnessValue() < config.getMaxFitness(); i++)
        {
            System.out.printf("Generation: %2d; Fittest: %2.0f; %s%n", i, fittest.calcFitnessValue(), fittest);

            population = population.evolve();

            fittest = population.getFittest();
        }

        System.out.println();
        System.out.println("Solution found!");
        System.out.printf("Genes: %s%n", fittest);
    }
}
