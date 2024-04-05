// Created: 29.06.2020
package de.freese.ga;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Basisklasse eines Genotypes für genetische Algorithmen.<br>
 * Genotype = Sammlung von Chromosomen / Lösungen
 *
 * @author Thomas Freese
 */
public abstract class Genotype {
    private final Chromosome[] chromosomes;
    private final Config config;

    protected Genotype(final Config config) {
        this(config, config.getSizeGenotype());
    }

    protected Genotype(final Config config, final int size) {
        super();

        this.config = Objects.requireNonNull(config, "config required");
        this.chromosomes = new Chromosome[size];
    }

    /**
     * Erzeugt ein neues leeres Chromosom / Lösung.
     */
    public abstract Chromosome createEmptyChromosome();

    /**
     * Erzeugt einen neuen leeren Genotype / Population.
     */
    public Genotype createEmptyGenotype() {
        return createEmptyGenotype(getConfig().getSizeGenotype());
    }

    /**
     * Erzeugt einen neuen leeren Genotype / Population.<br>
     */
    public abstract Genotype createEmptyGenotype(int size);

    /**
     * Zufällige Rekombination ausgewählter Individuen (Chromosomen).<br>
     * Beim Crossover werden aus der Population/Genotype paarweise Chromosomen ausgewählt, die dann mit einer Wahrscheinlichkeit W zu kreuzen sind.
     */
    public Chromosome crossover(final Chromosome parent1, final Chromosome parent2) {
        final Chromosome population = createEmptyChromosome();

        for (int i = 0; i < parent1.size(); i++) {
            final Gene gene;

            if (getConfig().getRandom().nextDouble() <= getConfig().getCrossoverRate()) {
                gene = parent1.getGene(i);
            }
            else {
                gene = parent2.getGene(i);
            }

            population.setGene(i, gene);
        }

        return population;
    }

    /**
     * 1. Zufällige Auswahl eines Individuums (Chromosom) für die Rekombination, {@link #tournamentSelection()}<br>
     * 2. Zufällige Rekombination ausgewählter Individuen (Chromosomen), {@link #crossover(Chromosome, Chromosome)}<br>
     * 3. Zufällige Veränderung der Gene, {@link Chromosome#mutate()}<br>
     */
    public Genotype evolve() {
        final Genotype newPopulation = createEmptyGenotype();

        int elitismOffset = 0;

        if (getConfig().isElitism()) {
            newPopulation.setChromosome(0, getFittest());
            elitismOffset = 1;
        }

        // for (int i = elitismOffset; i < size(); i++) {
        // // Loop over the population size and create new individuals with crossover.
        // // Select parents
        // Chromosome parent1 = tournamentSelection();
        // Chromosome parent2 = tournamentSelection();
        //
        // // Kann bei einigen Beispielen zur Endlos-Schleife führen.
        // // while(parent1.calcFitnessValue() == parent2.calcFitnessValue()) {
        // // parent2 = tournamentSelection();
        // // }
        //
        // // Crossover parents
        // Chromosome child = crossover(parent1, parent2);
        //
        // // Add child to new population
        // newPopulation.setChromosome(i, child);
        //
        // // Mutate population
        // child.mutate();
        // }

        IntStream.range(elitismOffset, size())
                .parallel()
                .map(i -> {
                    // Loop over the population size and create new individuals with crossover.
                    // Select parents
                    final Chromosome parent1 = tournamentSelection();
                    final Chromosome parent2 = tournamentSelection();

                    // Kann bei einigen Beispielen zur Endlos-Schleife führen.
                    // while(parent1.calcFitnessValue() == parent2.calcFitnessValue())  {
                    //     parent2 = tournamentSelection();
                    // }

                    // Crossover parents
                    final Chromosome child = crossover(parent1, parent2);

                    // Add child to new population
                    newPopulation.setChromosome(i, child);

                    return i;
                })
                .forEach(i -> {
                            // Mutate population
                            newPopulation.getChromosome(i).mutate();
                        }
                )
        ;

        return newPopulation;
    }

    public Chromosome getChromosome(final int index) {
        return getChromosomes()[index];
    }

    /**
     * Liefert das Chromosom mit dem höchsten Fitnesswert.
     */
    public Chromosome getFittest() {
        // Chromosome fittest = getChromosome(0);
        // double fittestFitness = fittest.calcFitnessValue();
        //
        // for (int i = 1; i < size(); i++)
        // {
        // final Chromosome chromosome = getChromosome(i);
        //
        // if (fittestFitness <= chromosome.calcFitnessValue()) {
        // fittest = chromosome;
        // fittestFitness = fittest.calcFitnessValue();
        // }
        // }
        //
        // return fittest;

        // Parallelisierung
        // final Supplier<NavigableMap<Double, Chromosome>> mapSupplier = () -> Collections.synchronizedNavigableMap(new TreeMap<>());

        // @formatter:off
        final NavigableMap<Double, Chromosome> map = Stream.of(getChromosomes())
                .parallel()
                .collect(Collectors.toMap(Chromosome::calcFitnessValue, Function.identity(), (a, b) -> a, TreeMap::new))
                ;
        // @formatter:on

        final Entry<Double, Chromosome> entry = map.lastEntry();

        return entry.getValue();
    }

    /**
     * Befüllt den Genotype mit Chromosomen.
     */
    public void populate() {
        for (int i = 0; i < size(); i++) {
            final Chromosome chromosome = createEmptyChromosome();
            chromosome.populate();
            setChromosome(i, chromosome);
        }
    }

    public void setChromosome(final int index, final Chromosome chromosome) {
        getChromosomes()[index] = chromosome;
    }

    /**
     * Liefert die Größe des Genotypes, Anzahl von Chromosomen.
     */
    public int size() {
        return getChromosomes().length;
    }

    /**
     * Zufällige Auswahl eines Individuums (Chromosom) für die Rekombination.<br>
     * (survival of the fittest)
     */
    public Chromosome tournamentSelection() {
        final Genotype tournament = createEmptyGenotype(getConfig().getTournamentSize());

        for (int i = 0; i < getConfig().getTournamentSize(); i++) {
            final int randomID = getConfig().getRandom().nextInt(size());

            tournament.setChromosome(i, getChromosome(randomID));
        }

        return tournament.getFittest();
    }

    protected Chromosome[] getChromosomes() {
        return this.chromosomes;
    }

    protected Config getConfig() {
        return this.config;
    }
}
