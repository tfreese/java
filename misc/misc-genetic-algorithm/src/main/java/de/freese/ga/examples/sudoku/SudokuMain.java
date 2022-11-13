// Created: 29.06.2020
package de.freese.ga.examples.sudoku;

import java.io.InputStream;
import java.util.List;

import de.freese.ga.Chromosome;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public final class SudokuMain
{
    public static void main(final String[] args) throws Exception
    {
        SudokuConfig config = new SudokuConfig();
        List<String[]> puzzle = null;

        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("sudoku_easy_1.txt"))
        {
            puzzle = config.parsePuzzle(inputStream);
        }

        config.setElitism(false);
        config.setSizeGenotype(5000); // Anzahl Chromosomen/Lösungen
        config.setTournamentSize(9);
        config.setMutationRate(0.01D); // 1 %
        config.setCrossoverRate(0.01D); // 1 %
        config.setPuzzle(puzzle);

        double maxFitness = config.getMaxFitness();

        // Create an initial population
        Genotype population = new SudokuGenotype(config);
        population.populate();

        Chromosome fittest = population.getFittest();

        for (int i = 0; i < config.getSizeGenotype(); i++)
        {
            double fitness = fittest.calcFitnessValue();

            System.out.printf("Generation: %2d; Fittest: %3.0f of %3.0f; %s%n", i, fitness, maxFitness, fittest);

            if (Double.compare(fitness, maxFitness) == 0)
            {
                break;
            }

            population = population.evolve();

            fittest = population.getFittest();

            // if (i == (config.getSizeGenotype() - 1))
            // {
            // // Neustart
            // i = 0;
            // population = new SudokuGenotype(config);
            // population.populate();
            //
            // fittest = population.getFittest();
            // }
        }

        // 1215 = 3 x 405: In allen Zeilen, Spalten und Blöcken ist die Summe 45.
        double fitness = fittest.calcFitnessValue();

        System.out.println(Double.compare(fitness, maxFitness) != 0 ? "Wrong Solution !!!" : "Solution found!");
        System.out.printf("Genes: Fittest: %3.0f of %3.0f%s%n", fitness, maxFitness, fittest);
    }

    private SudokuMain()
    {
        super();
    }
}
