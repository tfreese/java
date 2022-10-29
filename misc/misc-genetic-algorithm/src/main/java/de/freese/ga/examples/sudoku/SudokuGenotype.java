// Created: 29.06.2020
package de.freese.ga.examples.sudoku;

import de.freese.ga.Chromosome;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public class SudokuGenotype extends Genotype
{
    /**
     * Erstellt ein neues {@link SudokuGenotype} Object.
     *
     * @param config {@link SudokuConfig}
     */
    public SudokuGenotype(final SudokuConfig config)
    {
        super(config);
    }

    /**
     * Erstellt ein neues {@link SudokuGenotype} Object.
     *
     * @param config {@link SudokuConfig}
     * @param size int
     */
    public SudokuGenotype(final SudokuConfig config, final int size)
    {
        super(config, size);
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyChromosome()
     */
    @Override
    public Chromosome createEmptyChromosome()
    {
        return new SudokuChromosome(getConfig());
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyGenotype(int)
     */
    @Override
    public Genotype createEmptyGenotype(final int size)
    {
        return new SudokuGenotype(getConfig(), size);
    }

    /**
     * @see de.freese.ga.Genotype#getConfig()
     */
    @Override
    protected SudokuConfig getConfig()
    {
        return (SudokuConfig) super.getConfig();
    }
}
