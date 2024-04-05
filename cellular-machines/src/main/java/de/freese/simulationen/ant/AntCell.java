// Created: 11.03.2021
package de.freese.simulationen.ant;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.freese.simulationen.model.AbstractCell;

/**
 * Zelle der Langton-Ameisen Simulation.
 *
 * @author Thomas Freese
 */
public class AntCell extends AbstractCell {
    /**
     * @author Thomas Freese
     */
    public enum CellType {
        ANT,
        BLACK,
        EMPTY,
        WHITE
    }

    /**
     * @author Thomas Freese
     */
    private enum Direction {
        EAST(+1, 0),
        NORTH(0, +1),
        SOUTH(0, -1),
        WEST(-1, 0);

        private static final List<Direction> DIRECTIONS;

        static {
            DIRECTIONS = new ArrayList<>();
            DIRECTIONS.add(NORTH);
            DIRECTIONS.add(EAST);
            DIRECTIONS.add(SOUTH);
            DIRECTIONS.add(WEST);
        }

        private final int frontOffsetX;

        private final int frontOffsetY;

        Direction(final int frontOffsetX, final int frontOffsetY) {
            this.frontOffsetX = frontOffsetX;
            this.frontOffsetY = frontOffsetY;
        }

        /**
         * Relative Koordinaten für die Zelle vor der Ameise.
         */
        public int[] getFrontOffsets() {
            return new int[]{this.frontOffsetX, this.frontOffsetY};
        }

        /**
         * Dreht die Richtung nach links.
         */
        public Direction turnLeft() {
            int index = DIRECTIONS.indexOf(this);

            if (index == 0) {
                index = DIRECTIONS.size() - 1;
            }
            else {
                index--;
            }

            return DIRECTIONS.get(index);
        }

        /**
         * Dreht die Richtung nach rechts.
         */
        public Direction turnRight() {
            int index = DIRECTIONS.indexOf(this);

            if (index == (DIRECTIONS.size() - 1)) {
                index = 0;
            }
            else {
                index++;
            }

            return DIRECTIONS.get(index);
        }
    }

    private CellType cellType;
    private Direction direction = Direction.NORTH;

    public AntCell(final AntRasterSimulation simulation) {
        super(simulation);
    }

    public boolean isAnt() {
        return CellType.ANT.equals(this.cellType);
    }

    public boolean isEmpty() {
        return CellType.EMPTY.equals(this.cellType);
    }

    /**
     * <ol>
     * <li>Ist das Feld vor ihr weiss, so stellt sie sich drauf, färbt es schwarz und dreht sich um 90 Grad nach rechts.</li>
     * <li>Ist das Feld vor ihr schwarz, so stellt sie sich drauf, färbt es weiss und dreht sich um 90 Grad nach links.</li>
     * </ol>
     */
    @Override
    public void nextGeneration() {
        if (!isAnt()) {
            return;
        }

        final int[] frontOffsets = this.direction.getFrontOffsets();
        final int newX = getSimulation().getXTorusKoord(getX(), frontOffsets[0]);
        final int newY = getSimulation().getYTorusKoord(getY(), frontOffsets[1]);

        final AntCell frontCell = getSimulation().getCell(newX, newY);

        if (frontCell.isAnt()) {
            // Nicht auf eine andere Ameise treten.
            setDirection(getSimulation().getRandomDirection());

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addNextGeneration(this);

            return;
        }

        if (Color.WHITE.equals(frontCell.getColor()) || frontCell.isEmpty()) {
            frontCell.setCellType(CellType.ANT);
            setCellType(CellType.BLACK);
            frontCell.direction = this.direction.turnRight();

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addNextGeneration(frontCell);
        }
        else if (Color.BLACK.equals(frontCell.getColor()) || frontCell.isEmpty()) {
            frontCell.setCellType(CellType.ANT);
            setCellType(CellType.WHITE);
            frontCell.direction = this.direction.turnLeft();

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addNextGeneration(frontCell);
        }
        else {
            // Verhindert 'fest steckende' Ameisen.
            setDirection(getSimulation().getRandomDirection());

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addNextGeneration(this);
        }
    }

    public void setCellType(final CellType cellType) {
        this.cellType = cellType;

        switch (cellType) {
            case ANT -> setColor(Color.RED);
            case BLACK -> setColor(Color.BLACK);
            case WHITE -> setColor(Color.WHITE);

            default -> setColor(Color.LIGHT_GRAY);
        }
    }

    /**
     * Setzt die Ausrichtung.
     *
     * @param orientation int; 0 - 3
     */
    public void setDirection(final int orientation) {
        this.direction = switch (orientation) {
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    @Override
    protected AntRasterSimulation getSimulation() {
        return (AntRasterSimulation) super.getSimulation();
    }
}
