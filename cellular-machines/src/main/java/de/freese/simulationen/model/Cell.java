package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Einzelne Zelle einer Welt.
 *
 * @author Thomas Freese
 * @since 28.09.2009
 */
public interface Cell {
    Color getColor();

    int getX();

    int getY();

    /**
     * Berechnet die nächste Generation.
     */
    void nextGeneration();
}
