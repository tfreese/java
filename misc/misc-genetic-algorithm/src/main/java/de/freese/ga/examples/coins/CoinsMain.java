// Created: 29.06.2020
package de.freese.ga.examples.coins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.freese.ga.Chromosome;
import de.freese.ga.Gene;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public final class CoinsMain {
    static void main() {
        final List<Integer> existingCoins = new ArrayList<>();
        // existingCoins.add(50);
        existingCoins.add(50);
        existingCoins.add(20);
        existingCoins.add(20);
        existingCoins.add(10);
        existingCoins.add(10);
        existingCoins.add(5);
        existingCoins.add(5);
        existingCoins.add(2);
        existingCoins.add(2);
        existingCoins.add(1);
        existingCoins.add(1);

        final CoinConfig config = new CoinConfig();
        // config.setElitism(false);
        config.setSizeGenotype(50); // Anzahl Chromosomen/Lösungen
        config.setExistingCoins(existingCoins);
        config.setTargetCents(63); // max. 99 Cent

        // Create an initial population
        Genotype population = new CoinGenotype(config);
        population.populate();

        Chromosome fittest = population.getFittest();

        // for (int i = 0; i < config.getSizeGenotype(); i++)
        for (int i = 0; fittest.calcFitnessValue() < config.getMaxFitness(); i++) {
            // %8.3f = 8 Stellen, 3 davon nach dem Komma.
            System.out.printf("Generation: %2d; Fittest: %8.3f; %s = %d Cent%n", i, fittest.calcFitnessValue(), fittest,
                    Stream.of(fittest.getGenes()).mapToInt(Gene::getInteger).sum());

            population = population.evolve();

            fittest = population.getFittest();
        }

        System.out.println();
        System.out.println("Solution found!");
        System.out.printf("Genes: %s = %d Cent%n", fittest, config.getTargetCents());
    }

    private CoinsMain() {
        super();
    }
}
