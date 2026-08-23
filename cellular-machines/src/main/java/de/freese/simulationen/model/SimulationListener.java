package de.freese.simulationen.model;

/**
 * Listener für Veränderungen einer Simulation.
 *
 * @author Thomas Freese
 * @since 17.10.2009
 */
@FunctionalInterface
public interface SimulationListener {
    /**
     * Wird aufgerufen, wenn ein Simulations-Durchgang beendet ist.
     */
    void completed(Simulation simulation);
}
