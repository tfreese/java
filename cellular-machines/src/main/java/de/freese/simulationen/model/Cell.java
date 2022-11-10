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
    Color getColor();

    int getX();

    int getY();

    /**
     * Berechnet die nächste Generation.
     */
    void nextGeneration();
}
