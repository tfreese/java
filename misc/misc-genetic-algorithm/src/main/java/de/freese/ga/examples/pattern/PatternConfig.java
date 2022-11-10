// Created: 29.06.2020
package de.freese.ga.examples.pattern;

import de.freese.ga.Config;

/**
 * @author Thomas Freese
 */
public class PatternConfig extends Config
{
    private boolean[] solution;

    public PatternConfig()
    {
        super();
    }

    /**
     * @see de.freese.ga.Config#getMaxFitness()
     */
    @Override
    public double getMaxFitness()
    {
        // Max. Wert, wenn alle Gene richtig sind.
        return getSizeChromosome();
    }

    public void setPattern(final String pattern)
    {
        setSizeChromosome(pattern.length());

        this.solution = new boolean[pattern.length()];

        for (int i = 0; i < pattern.length(); i++)
        {
            char character = pattern.charAt(i);

            this.solution[i] = character == '1';
        }
    }

    boolean[] getSolution()
    {
        return this.solution;
    }
}
