// Created: 28.09.2009
package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Einzelne Zelle einer Welt.
 *
 * @author Thomas Freese
 */
public interface Cell
{
    /**
     * @return {@link Color}
     */
    Color getColor();

    /**
     * @return int
     */
    int getX();

    /**
     * @return int
     */
    int getY();

    /**
     * Berechnet die nächste Generation.
     */
    void nextGeneration();
}
