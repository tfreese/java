// Created: 26.01.2014
package de.freese.simulationen.model;

import java.awt.Image;

/**
 * Interface für eine Simulationsumgebung.
 *
 * @author Thomas Freese
 */
public interface Simulation
{
    void addWorldListener(final SimulationListener simulationListener);

    /**
     * Höhe in Pixeln.
     */
    int getHeight();

    /**
     * Liefert das Bild der zuletzt berechneten Generation.
     */
    Image getImage();

    /**
     * Breite in Pixeln.
     */
    int getWidth();

    /**
     * Berechnet die nächste Generation.
     */
    void nextGeneration();

    /**
     * Neustart der Simulation.
     */
    void reset();
}
