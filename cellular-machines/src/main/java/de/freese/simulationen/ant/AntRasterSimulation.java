// Created: 11.03.2021
package de.freese.simulationen.ant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.freese.simulationen.ant.AntCell.CellType;
import de.freese.simulationen.model.AbstractRasterSimulation;
import de.freese.simulationen.model.Cell;

/**
 * @author Thomas Freese
 */
public class AntRasterSimulation extends AbstractRasterSimulation
{
    /**
     * Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
     */
    private final Set<AntCell> ants = new HashSet<>();
    /**
     * Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
     */
    private final Set<AntCell> antsNextGeneration = Collections.synchronizedSet(new HashSet<>());
    /**
     *
     */
    private final int numberOfAnts;

    /**
     * Erstellt ein neues {@link AntRasterSimulation} Object.<br>
     * Anzahl Ameisen bei 640x480 = Math.sqrt(width * height) / 3 ≈ 185
     *
     * @param width int
     * @param height int
     */
    public AntRasterSimulation(final int width, final int height)
    {
        this(width, height, (int) Math.sqrt((double) width * height) / 3);
    }

    /**
     * Erstellt ein neues {@link AntRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     * @param numberOfAnts int
     */
    public AntRasterSimulation(final int width, final int height, final int numberOfAnts)
    {
        super(width, height);

        this.numberOfAnts = numberOfAnts;

        fillRaster(() -> new AntCell(this));
        reset();
    }

    /**
     * @param cell {@link AntCell}
     */
    void addNextGeneration(final AntCell cell)
    {
        this.antsNextGeneration.add(cell);
    }

    /**
     * @see de.freese.simulationen.model.AbstractRasterSimulation#getCell(int, int)
     */
    @Override
    protected AntCell getCell(final int x, final int y)
    {
        return (AntCell) super.getCell(x, y);
    }

    /**
     * Liefert eine zufällige Marsch-Richtung.
     *
     * @return int; 0 - 3
     */
    int getRandomDirection()
    {
        return getRandom().nextInt(4);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // Hier würden sämtliche Zellen verarbeitet werden, ist bei den Ameisen jedoch unnötig.
        // getCellStream().forEach(RasterCell::nextGeneration);

        // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
        this.ants.clear();
        this.ants.addAll(this.antsNextGeneration);
        this.antsNextGeneration.clear();

        this.ants.forEach(Cell::nextGeneration);

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.model.AbstractRasterSimulation#reset()
     */
    @Override
    public void reset()
    {
        getCellStream().map(AntCell.class::cast).forEach(c -> c.setCellType(CellType.EMPTY));

        for (int i = 0; i < this.numberOfAnts; i++)
        {
            // int x = getRandom().nextInt(50) + minX;
            // int y = getRandom().nextInt(50) + minY;
            int x = getRandom().nextInt(getWidth());
            int y = getRandom().nextInt(getHeight());

            AntCell cell = getCell(x, y);
            cell.setCellType(CellType.ANT);
            cell.setDirection(getRandomDirection());

            addNextGeneration(cell);
        }

        fireCompleted();
    }
}
