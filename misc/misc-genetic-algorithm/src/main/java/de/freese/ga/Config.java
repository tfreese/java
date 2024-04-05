// Created: 29.06.2020
package de.freese.ga;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Thomas Freese
 */
public class Config {
    private final Random random;
    /**
     * 50 %
     */
    private double crossoverRate = 0.5D;
    private boolean elitism = true;
    private double maxFitness;
    /**
     * 1,5%
     */
    private double mutationRate = 0.015D;
    private int sizeChromosome;
    private int sizeGenotype;
    private int tournamentSize = 5;

    public Config() {
        super();

        // this.random = new Random();
        this.random = new SecureRandom();
    }

    /**
     * Liefert die Rate der Vererbung eines Chromosoms.
     */
    public double getCrossoverRate() {
        return this.crossoverRate;
    }

    /**
     * Liefert, wenn möglich, den max. Wert der Fitnessfunktion.
     */
    public double getMaxFitness() {
        return this.maxFitness;
    }

    /**
     * Liefert die Rate der Mutation eines Chromosoms.
     */
    public double getMutationRate() {
        return this.mutationRate;
    }

    public Random getRandom() {
        return this.random;
    }

    /**
     * Liefert die Größe des Chromosoms.
     */
    public int getSizeChromosome() {
        return this.sizeChromosome;
    }

    /**
     * Liefert die Größe des Genotyps.
     */
    public int getSizeGenotype() {
        return this.sizeGenotype;
    }

    /**
     * Liefert die Größe der natürlichen Selektion für einen Genotyp.
     */
    public int getTournamentSize() {
        return this.tournamentSize;
    }

    /**
     * Beim Elitismus wird das fitteste Chromosom in die nächste Generation übernommen.
     */
    public boolean isElitism() {
        return this.elitism;
    }

    /**
     * Setzt die Rate der Vererbung eines Chromosoms.
     */
    public void setCrossoverRate(final double rate) {
        this.crossoverRate = rate;
    }

    /**
     * Beim Elitismus wird das fitteste Chromosom in die nächste Generation übernommen.
     */
    public void setElitism(final boolean elitism) {
        this.elitism = elitism;
    }

    public void setMaxFitness(final double maxFitness) {
        this.maxFitness = maxFitness;
    }

    /**
     * Setzt die Rate der Mutation eines Chromosoms.
     */
    public void setMutationRate(final double rate) {
        this.mutationRate = rate;
    }

    /**
     * Setzt die Größe des Chromosoms.
     */
    public void setSizeChromosome(final int size) {
        this.sizeChromosome = size;
    }

    /**
     * Setzt die Größe des Genotyps.
     */
    public void setSizeGenotype(final int size) {
        this.sizeGenotype = size;
    }

    /**
     * Setzt die Größe der natürlichen Selektion für einen Genotyp.
     */
    public void setTournamentSize(final int size) {
        this.tournamentSize = size;
    }
}
