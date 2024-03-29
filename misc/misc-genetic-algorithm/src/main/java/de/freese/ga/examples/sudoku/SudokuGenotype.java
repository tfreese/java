// Created: 29.06.2020
package de.freese.ga.examples.sudoku;

import de.freese.ga.Chromosome;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public class SudokuGenotype extends Genotype {
    public SudokuGenotype(final SudokuConfig config) {
        super(config);
    }

    public SudokuGenotype(final SudokuConfig config, final int size) {
        super(config, size);
    }

    @Override
    public Chromosome createEmptyChromosome() {
        return new SudokuChromosome(getConfig());
    }

    @Override
    public Genotype createEmptyGenotype(final int size) {
        return new SudokuGenotype(getConfig(), size);
    }

    @Override
    protected SudokuConfig getConfig() {
        return (SudokuConfig) super.getConfig();
    }
}
