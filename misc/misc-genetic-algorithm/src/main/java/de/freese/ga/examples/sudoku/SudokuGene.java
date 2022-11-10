// Created: 29.06.2020
package de.freese.ga.examples.sudoku;

import java.util.Objects;

import de.freese.ga.Gene;

/**
 * @author Thomas Freese
 */
public class SudokuGene extends Gene
{
    private final boolean mutable;

    public SudokuGene(final Integer value, final boolean mutable)
    {
        super();

        super.setValue(value);
        this.mutable = mutable;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof SudokuGene gene))
        {
            return false;
        }

        if (!super.equals(o))
        {
            return false;
        }

        return isMutable() == gene.isMutable();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), isMutable());
    }

    /**
     * Beim Sudoku dürfen die fest vorgegebenen Zahlen nicht verändert werden !
     */
    public boolean isMutable()
    {
        return this.mutable;
    }

    /**
     * @see de.freese.ga.Gene#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final Object value)
    {
        if (!isMutable())
        {
            return;
        }

        super.setValue(value);
    }

    /**
     * @see de.freese.ga.Gene#toString()
     */
    @Override
    public String toString()
    {
        return super.toString() + "; " + isMutable();
    }
}
