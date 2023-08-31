// Created: 28.09.2009
package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Dummy-Zelle für leere Flächen.
 *
 * @author Thomas Freese
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
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(": ");
        sb.append("Color[r=").append(getColor().getRed()).append(",g=").append(getColor().getGreen()).append(",b=").append(getColor().getBlue()).append("]");

        return sb.toString();
    }
}
