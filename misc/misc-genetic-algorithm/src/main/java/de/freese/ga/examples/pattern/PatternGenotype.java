// Created: 29.06.2020
package de.freese.ga.examples.pattern;

import de.freese.ga.Chromosome;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public class PatternGenotype extends Genotype
{
    public PatternGenotype(final PatternConfig config)
    {
        super(config);
    }

    private PatternGenotype(final PatternConfig config, final int size)
    {
        super(config, size);
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyChromosome()
     */
    @Override
    public Chromosome createEmptyChromosome()
    {
        return new PatternChromosome(getConfig());
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyGenotype(int)
     */
    @Override
    public Genotype createEmptyGenotype(final int size)
    {
        return new PatternGenotype(getConfig(), size);
    }

    /**
     * @see de.freese.ga.Genotype#getConfig()
     */
    @Override
    protected PatternConfig getConfig()
    {
        return (PatternConfig) super.getConfig();
    }
}
