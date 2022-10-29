// Created: 29.06.2020
package de.freese.ga.examples.travelling_salesman;

import de.freese.ga.Chromosome;
import de.freese.ga.Genotype;

/**
 * @author Thomas Freese
 */
public class TspGenotype extends Genotype
{
    /**
     * Erstellt ein neues {@link TspGenotype} Object.
     *
     * @param config {@link TspConfig}
     */
    public TspGenotype(final TspConfig config)
    {
        super(config);
    }

    /**
     * Erstellt ein neues {@link TspGenotype} Object.
     *
     * @param config {@link TspConfig}
     * @param size int
     */
    public TspGenotype(final TspConfig config, final int size)
    {
        super(config, size);
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyChromosome()
     */
    @Override
    public Chromosome createEmptyChromosome()
    {
        return new TspChromosome(getConfig());
    }

    /**
     * @see de.freese.ga.Genotype#createEmptyGenotype(int)
     */
    @Override
    public Genotype createEmptyGenotype(final int size)
    {
        return new TspGenotype(getConfig(), size);
    }

    /**
     * @see de.freese.ga.Genotype#crossover(de.freese.ga.Chromosome, de.freese.ga.Chromosome)
     */
    @Override
    public Chromosome crossover(final Chromosome parent1, final Chromosome parent2)
    {
        // Create new child tour
        Chromosome childChromosome = createEmptyChromosome();

        // Get start and end sub tour positions for parent1's tour
        int startPos = getConfig().getRandom().nextInt(parent1.size());
        int endPos = getConfig().getRandom().nextInt(parent1.size());

        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < childChromosome.size(); i++)
        {
            // If our start position is less than the end position
            if ((startPos < endPos) && (i > startPos) && (i < endPos))
            {
                childChromosome.setGene(i, parent1.getGene(i));
            } // If our start position is larger
            else if (startPos > endPos)
            {
                if (!((i < startPos) && (i > endPos)))
                {
                    childChromosome.setGene(i, parent1.getGene(i));
                }
            }
        }

        // Loop through parent2's city tour
        for (int i = 0; i < parent2.size(); i++)
        {
            // If child doesn't have the city add it
            if (!childChromosome.contains(parent2.getGene(i)))
            {
                // Loop to find a spare position in the child's tour
                for (int ii = 0; ii < childChromosome.size(); ii++)
                {
                    // Spare position found, add city
                    if (childChromosome.getGene(ii) == null)
                    {
                        childChromosome.setGene(ii, parent2.getGene(i));
                        break;
                    }
                }
            }
        }

        return childChromosome;
    }

    /**
     * @see de.freese.ga.Genotype#evolve()
     */
    @Override
    public TspGenotype evolve()
    {
        return (TspGenotype) super.evolve();
    }

    /**
     * @see de.freese.ga.Genotype#getConfig()
     */
    @Override
    protected TspConfig getConfig()
    {
        return (TspConfig) super.getConfig();
    }

    /**
     * @see de.freese.ga.Genotype#getFittest()
     */
    @Override
    public TspChromosome getFittest()
    {
        return (TspChromosome) super.getFittest();
    }
}
