// Created: 11.03.2021
package de.freese.simulationen.gameoflife;

import de.freese.simulationen.model.AbstractRasterSimulation;
import de.freese.simulationen.model.Cell;

/**
 * @author Thomas Freese
 */
public class GoFRasterSimulation extends AbstractRasterSimulation {
    public GoFRasterSimulation(final int width, final int height) {
        super(width, height);

        fillRaster(() -> new GoFCell(this));
        reset();
    }

    @Override
    public void nextGeneration() {
        getCellStream().map(GoFCell.class::cast).forEach(GoFCell::ermittleLebendeNachbarn);
        getCellStream().forEach(Cell::nextGeneration);

        fireCompleted();
    }

    @Override
    protected GoFCell getCell(final int x, final int y) {
        return (GoFCell) super.getCell(x, y);
    }

    @Override
    protected void reset(final int x, final int y) {
        getCell(x, y).setAlive(getRandom().nextBoolean());
    }
}
