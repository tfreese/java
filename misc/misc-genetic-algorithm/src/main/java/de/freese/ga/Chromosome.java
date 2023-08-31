// Created: 29.06.2020
package de.freese.ga;

import java.util.Objects;

/**
 * Basisklasse eines Chromosomes für genetische Algorithmen.<br>
 * Chromosome = Mögliche Lösung
 *
 * @author Thomas Freese
 */
public abstract class Chromosome {
    private final Config config;

    private final Gene[] genes;

    protected Chromosome(final Config config) {
        super();

        this.config = Objects.requireNonNull(config, "config required");
        this.genes = new Gene[config.getSizeChromosome()];
    }

    public abstract double calcFitnessValue();

    public boolean contains(final Gene gene) {
        boolean contains = false;

        for (Gene g : getGenes()) {
            if (g == null) {
                continue;
            }

            if (g.equals(gene)) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    public Gene getGene(final int index) {
        return getGenes()[index];
    }

    public Gene[] getGenes() {
        return this.genes;
    }

    /**
     * Zufällige Veränderung der Gene.<br>
     * Die Mutation verändert zufällig ein oder mehrere Gene eines Chromosoms.
     */
    public void mutate() {
        for (int i = 0; i < size(); i++) {
            if (getConfig().getRandom().nextDouble() < getConfig().getMutationRate()) {
                int j = getConfig().getRandom().nextInt(size());

                Gene gene1 = getGene(i);
                Gene gene2 = getGene(j);

                setGene(j, gene1);
                setGene(i, gene2);
            }
        }

        // @formatter:off
//        IntStream.range(0, chromosome.size())
//            .parallel()
//            .forEach(i -> {
//                if (getRandom().nextDouble() < getMutationRate())
//                {
//                    int j = getRandom().nextInt(chromosome.size());
//
//                    G gene1 = chromosome.getGene(i);
//                    G gene2 = chromosome.getGene(j);
//
//                    chromosome.setGene(j, gene1);
//                    chromosome.setGene(i, gene2);
//                }
//            });
        // @formatter:on
    }

    /**
     * Befüllt das Chromosom mit Genen.<br>
     */
    public abstract void populate();

    public void setGene(final int index, final Gene gene) {
        getGenes()[index] = gene;
    }

    /**
     * Liefert die Größe des Chromosoms, Anzahl an Genen.
     */
    public int size() {
        return getGenes().length;
    }

    protected Config getConfig() {
        return this.config;
    }
}
