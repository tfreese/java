// Created: 29.06.2020
package de.freese.ga.examples.travelling_salesman;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://www.theprojectspot.com/tutorial-post/applying-a-genetic-algorithm-to-the-travelling-salesman-problem/5">travelling-salesman-problem</a>
 *
 * @author Thomas Freese
 */
public final class TspMain {
    static void main() {
        final List<City> cities = new ArrayList<>(20);
        cities.add(new City("A", 60, 200));
        cities.add(new City("B", 180, 200));
        cities.add(new City("C", 80, 180));
        cities.add(new City("D", 140, 180));
        cities.add(new City("E", 20, 160));
        cities.add(new City("F", 100, 160));
        cities.add(new City("G", 200, 160));
        cities.add(new City("H", 140, 140));
        cities.add(new City("I", 40, 120));
        cities.add(new City("J", 100, 120));
        cities.add(new City("K", 180, 100));
        cities.add(new City("L", 60, 80));
        cities.add(new City("M", 120, 80));
        cities.add(new City("N", 180, 60));
        cities.add(new City("O", 20, 40));
        cities.add(new City("P", 100, 40));
        cities.add(new City("Q", 200, 40));
        cities.add(new City("R", 20, 20));
        cities.add(new City("S", 60, 20));
        cities.add(new City("T", 160, 20));

        final TspConfig config = new TspConfig();
        // config.setElitism(false);
        config.setSizeGenotype(200); // Anzahl Chromosomen/Lösungen/Tour
        config.setCities(cities);
        // config.setTournamentSize((int) (config.getSizeGenotype() * 0.25D)); // Tournament auf 25% der Genotype-Größe.

        // Create an initial population
        TspGenotype population = new TspGenotype(config);
        population.populate();

        TspChromosome fittest = population.getFittest();

        for (int i = 0; i < config.getSizeGenotype(); i++) {
            System.out.printf("Generation: %2d; Fittest: %2.9f; Distance: %4.3f; %s%n", i, fittest.calcFitnessValue(), fittest.getDistance(), fittest);

            population = population.evolve();

            fittest = population.getFittest();
        }

        // Print final results: minimum distance found: 871,117
        System.out.println();
        System.out.println("Solution found!");
        System.out.printf("Genes: %s; Final distance: %4.3f%n", fittest, fittest.getDistance());
    }

    private TspMain() {
        super();
    }
}
