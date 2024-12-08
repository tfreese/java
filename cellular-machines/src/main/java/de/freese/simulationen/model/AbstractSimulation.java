// Created: 18.09.2009
package de.freese.simulationen.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * BasisModel für die Simulationen.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSimulation implements Simulation {
    /**
     * Liefert die entsprechende Torus-Koordinate.
     *
     * @param size int, Grösse des Simulationsfeldes
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     */
    private static int getTorusCoord(final int size, final int pos, final int offSet) {
        if (pos == 0 && offSet < 0) {
            return size + offSet;
        }

        return ((size + 1) * (pos + offSet)) % size;
    }

    private final int height;
    private final Random random;
    private final List<SimulationListener> simulationListeners;
    private final int width;

    protected AbstractSimulation(final int width, final int height) {
        super();

        this.width = width;
        this.height = height;
        this.random = new SecureRandom();
        this.simulationListeners = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void addWorldListener(final SimulationListener simulationListener) {
        if (!simulationListeners.contains(simulationListener)) {
            simulationListeners.add(simulationListener);
        }
    }

    @Override
    public int getHeight() {
        return height;
    }

    public Random getRandom() {
        return random;
    }

    @Override
    public int getWidth() {
        return width;
    }

    /**
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     */
    public int getXTorusCoord(final int pos, final int offSet) {
        return getTorusCoord(getWidth(), pos, offSet);
    }

    /**
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     */
    public int getYTorusCoord(final int pos, final int offSet) {
        return getTorusCoord(getHeight(), pos, offSet);
    }

    /**
     * Feuert das Event, wenn ein Simulations-Durchgang beendet ist.
     */
    protected void fireCompleted() {
        updateImage();

        for (SimulationListener listener : simulationListeners) {
            listener.completed(this);
        }
    }

    /**
     * Aktualisiert die ImageSource mit den neuen Pixeln.<br>
     * Image wird auf den neuesten Stand gebracht.<br>
     * Wird in der Methode {@link #fireCompleted()} aufgerufen.
     */
    protected abstract void updateImage();
}
