// Created: 29.06.2020
package de.freese.ga;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Thomas Freese
 */
public class Config
{
    /**
     *
     */
    private final Random random;
    /**
     * 50 %
     */
    private double crossoverRate = 0.5D;
    /**
     *
     */
    private boolean elitism = true;
    /**
     *
     */
    private double maxFitness = 0.0D;
    /**
     * 1,5%
     */
    private double mutationRate = 0.015D;
    /**
     *
     */
    private int sizeChromosome = 0;
    /**
     *
     */
    private int sizeGenotype = 0;
    /**
     *
     */
    private int tournamentSize = 5;

    /**
     * Erstellt ein neues {@link Config} Object.
     */
    public Config()
    {
        super();

        // this.random = new Random();
        this.random = new SecureRandom();
    }

    /**
     * Liefert die Rate der Vererbung eines Chromosoms.
     *
     * @return double
     */
    public double getCrossoverRate()
    {
        return this.crossoverRate;
    }

    /**
     * Liefert, wenn möglich, den max. Wert der Fitnessfunktion.
     *
     * @return double
     */
    public double getMaxFitness()
    {
        return this.maxFitness;
    }

    /**
     * Liefert die Rate der Mutation eines Chromosoms.
     *
     * @return double
     */
    public double getMutationRate()
    {
        return this.mutationRate;
    }

    /**
     * @return {@link Random}
     */
    public Random getRandom()
    {
        return this.random;
    }

    /**
     * Liefert die Größe des Chromosoms.
     *
     * @return int
     */
    public int getSizeChromosome()
    {
        return this.sizeChromosome;
    }

    /**
     * Liefert die Größe des Genotyps.
     *
     * @return int
     */
    public int getSizeGenotype()
    {
        return this.sizeGenotype;
    }

    /**
     * Liefert die Größe der natürlichen Selektion für einen Genotyp.
     *
     * @return int
     */
    public int getTournamentSize()
    {
        return this.tournamentSize;
    }

    /**
     * Beim Elitismus wird das fitteste Chromosom in die nächste Generation übernommen.
     *
     * @return boolean
     */
    public boolean isElitism()
    {
        return this.elitism;
    }

    /**
     * Setzt die Rate der Vererbung eines Chromosoms.
     *
     * @param rate double
     */
    public void setCrossoverRate(final double rate)
    {
        this.crossoverRate = rate;
    }

    /**
     * Beim Elitismus wird das fitteste Chromosom in die nächste Generation übernommen.
     *
     * @param elitism boolean
     */
    public void setElitism(final boolean elitism)
    {
        this.elitism = elitism;
    }

    /**
     * @param maxFitness double
     */
    public void setMaxFitness(final double maxFitness)
    {
        this.maxFitness = maxFitness;
    }

    /**
     * Setzt die Rate der Mutation eines Chromosoms.
     *
     * @param rate double
     */
    public void setMutationRate(final double rate)
    {
        this.mutationRate = rate;
    }

    /**
     * Setzt die Größe des Chromosoms.
     *
     * @param size int
     */
    public void setSizeChromosome(final int size)
    {
        this.sizeChromosome = size;
    }

    /**
     * Setzt die Größe des Genotyps.
     *
     * @param size int
     */
    public void setSizeGenotype(final int size)
    {
        this.sizeGenotype = size;
    }

    /**
     * Setzt die Größe der natürlichen Selektion für einen Genotyp.
     *
     * @param size int
     */
    public void setTournamentSize(final int size)
    {
        this.tournamentSize = size;
    }
}
