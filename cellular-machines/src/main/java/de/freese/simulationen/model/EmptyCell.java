package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Dummy-Zelle für leere Flächen.
 *
 * @author Thomas Freese
 * @since 28.09.2009
 */
public class EmptyCell extends AbstractCell {
    public EmptyCell(final AbstractRasterSimulation simulation) {
        super(simulation);
    }

    public EmptyCell(final AbstractRasterSimulation simulation, final Color color) {
        super(simulation, color);
    }

    @Override
    public void nextGeneration() {
        // Empty
    }

    @Override
    public String toString() {
        return super.toString() + ": " + "Color[r=" + getColor().getRed() + ",g=" + getColor().getGreen() + ",b=" + getColor().getBlue() + "]";
    }
}
