// Created: 11.03.2021
package de.freese.simulationen.gameoflife;

import de.freese.simulationen.model.AbstractRasterSimulation;
import de.freese.simulationen.model.Cell;

/**
 * @author Thomas Freese
 */
public class GoFRasterSimulation extends AbstractRasterSimulation
{
    /**
     * Erstellt ein neues {@link GoFRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     */
    public GoFRasterSimulation(final int width, final int height)
    {
        super(width, height);

        fillRaster(() -> new GoFCell(this));
        reset();
    }

    /**
     * @see de.freese.simulationen.model.AbstractRasterSimulation#getCell(int, int)
     */
    @Override
    protected GoFCell getCell(final int x, final int y)
    {
        return (GoFCell) super.getCell(x, y);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        getCellStream().map(GoFCell.class::cast).forEach(GoFCell::ermittleLebendeNachbarn);
        getCellStream().forEach(Cell::nextGeneration);

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.model.AbstractRasterSimulation#reset(int, int)
     */
    @Override
    protected void reset(final int x, final int y)
    {
        getCell(x, y).setAlive(getRandom().nextBoolean());
    }
}
