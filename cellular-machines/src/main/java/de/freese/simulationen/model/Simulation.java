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
    /**
     * Fügt einen neuen Listener hinzu.
     *
     * @param simulationListener {@link SimulationListener}
     */
    void addWorldListener(final SimulationListener simulationListener);

    /**
     * Höhe in Pixeln.
     *
     * @return int
     */
    int getHeight();

    /**
     * Liefert das Bild der zuletzt berechneten Generation.
     *
     * @return {@link Image}
     */
    Image getImage();

    /**
     * Breite in Pixeln.
     *
     * @return int
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
