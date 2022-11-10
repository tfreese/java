// Created: 18.09.2009
package de.freese.simulationen.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * BasisModel für die Simulationen.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSimulation implements Simulation
{
    private final int height;

    private final Random random;

    private final List<SimulationListener> simulationListeners;

    private final int width;

    protected AbstractSimulation(final int width, final int height)
    {
        super();

        this.width = width;
        this.height = height;
        this.random = new Random();
        this.simulationListeners = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * @see de.freese.simulationen.model.Simulation#addWorldListener(de.freese.simulationen.model.SimulationListener)
     */
    @Override
    public void addWorldListener(final SimulationListener simulationListener)
    {
        if (!this.simulationListeners.contains(simulationListener))
        {
            this.simulationListeners.add(simulationListener);
        }
    }

    /**
     * @see de.freese.simulationen.model.Simulation#getHeight()
     */
    @Override
    public int getHeight()
    {
        return this.height;
    }

    public Random getRandom()
    {
        return this.random;
    }

    /**
     * @see de.freese.simulationen.model.Simulation#getWidth()
     */
    @Override
    public int getWidth()
    {
        return this.width;
    }

    /**
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     */
    public int getXTorusKoord(final int pos, final int offSet)
    {
        return getTorusKoord(getWidth(), pos, offSet);
    }

    /**
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     */
    public int getYTorusKoord(final int pos, final int offSet)
    {
        return getTorusKoord(getHeight(), pos, offSet);
    }

    /**
     * Feuert das Event, wenn ein Simulations-Durchgang beendet ist.
     */
    protected void fireCompleted()
    {
        updateImage();

        for (SimulationListener listener : this.simulationListeners)
        {
            listener.completed(this);
        }
    }

    /**
     * Aktualisiert die ImageSource mit den neuen Pixeln.<br>
     * Image wird auf den neuesten Stand gebracht.<br>
     * Wird in der Methode {@link #fireCompleted()} aufgerufen.
     */
    protected abstract void updateImage();

    /**
     * Liefert die entsprechende Torus-Koordinate.
     *
     * @param size int, Grösse des Simulationsfeldes
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     */
    private int getTorusKoord(final int size, final int pos, final int offSet)
    {
        if ((pos == 0) && (offSet < 0))
        {
            return size + offSet;
        }

        return ((size + 1) * (pos + offSet)) % size;
    }
}
