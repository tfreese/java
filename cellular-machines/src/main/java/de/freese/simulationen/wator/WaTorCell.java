// Created: 11.03.2021
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.freese.simulationen.model.AbstractCell;

/**
 * @author Thomas Freese
 */
public class WaTorCell extends AbstractCell {
    /**
     * @author Thomas Freese
     */
    public enum CellType {
        EMPTY,
        FISH,
        SHARK
    }

    private final List<int[]> fischNachbarnList = new ArrayList<>(8);
    private final List<int[]> freieNachbarnList = new ArrayList<>(8);

    private CellType cellType;
    private int energy;

    public WaTorCell(final WaTorRasterSimulation simulation) {
        super(simulation);
    }

    public boolean isEmpty() {
        return CellType.EMPTY.equals(cellType);
    }

    public boolean isFish() {
        return CellType.FISH.equals(cellType);
    }

    public boolean isShark() {
        return CellType.SHARK.equals(cellType);
    }

    @Override
    public void nextGeneration() {
        if (CellType.FISH.equals(cellType)) {
            nextFish();
        }
        else if (CellType.SHARK.equals(cellType)) {
            nextShark();
        }
    }

    public void setCellType(final CellType cellType) {
        this.cellType = cellType;

        switch (cellType) {
            case FISH -> setColor(Color.GREEN);
            case SHARK -> setColor(Color.BLUE);

            default -> setColor(Color.BLACK);
        }
    }

    public void setEnergy(final int energy) {
        this.energy = energy;
    }

    void incrementEnergy() {
        energy++;
    }

    /**
     * Returns a fish in the neighborhood or null.
     */
    protected WaTorCell getFischNachbar() {
        if (fischNachbarnList.isEmpty()) {
            return null;
        }

        while (!fischNachbarnList.isEmpty()) {
            final int size = fischNachbarnList.size();
            final int[] coords = fischNachbarnList.remove(getSimulation().getRandom().nextInt(size));

            final WaTorCell cell = getSimulation().getCell(coords[0], coords[1]);

            // Ist das wirklich noch ein Fisch ?
            if (cell.isFish()) {
                return cell;
            }
        }

        return null;
    }

    @Override
    protected WaTorRasterSimulation getSimulation() {
        return (WaTorRasterSimulation) super.getSimulation();
    }

    private void decrementEnergy() {
        energy--;
    }

    private void decrementEnergy(final int delta) {
        energy -= delta;
    }

    /**
     * Determined the neighbors of this cell.<br>
     */
    private void ermittleNachbarn() {
        freieNachbarnList.clear();
        fischNachbarnList.clear();

        visitNeighbours((x, y) -> {
            final WaTorCell cell = getSimulation().getCell(x, y);

            if (cell.isEmpty()) {
                freieNachbarnList.add(new int[]{x, y});
            }
            else if (cell.isFish()) {
                fischNachbarnList.add(new int[]{x, y});
            }
        });
    }

    /**
     * LReturns the coordinates of a free cell in the neighborhood or null.
     */
    private int[] getFreierNachbar() {
        if (freieNachbarnList.isEmpty()) {
            return null;
        }

        while (!freieNachbarnList.isEmpty()) {
            final int size = freieNachbarnList.size();
            final int[] coords = freieNachbarnList.remove(getSimulation().getRandom().nextInt(size));

            final WaTorCell cell = getSimulation().getCell(coords[0], coords[1]);

            // Ist die Stelle wirklich noch frei ?
            if (cell.isEmpty()) {
                return coords;
            }
        }

        return null;
    }

    private void incrementEnergy(final int delta) {
        energy += delta;
    }

    /**
     * <ol>
     * <li>Jeder Fisch schwimmt zufällig auf eines der vier angrenzenden Felder, sofern es leer ist.</li>
     * <li>Mit jedem Durchgang gewinnt der Fisch einen Energiepunkt.</li>
     * <li>Übersteigt die Energie den Wert für die Erzeugung eines Nachkommen ("Breed Energy"), so wird ein neuer Fisch auf einem angrenzenden freien Feld
     * geboren. Die vorhandene Energie wird gleichmässig zwischen Eltern- und Kind-Fisch verteilt.</li>
     * </ol>
     */
    private void nextFish() {
        ermittleNachbarn();

        incrementEnergy();

        final int[] frei = getFreierNachbar();

        if (frei != null) {
            final int freiX = frei[0];
            final int freiY = frei[1];

            // Move the fisch.
            final WaTorCell cell = getSimulation().getCell(freiX, freiY);
            cell.setCellType(CellType.FISH);
            cell.setEnergy(energy);

            if (energy >= getSimulation().getFishBreedEnergy()) {
                // Nachwuchs auf diesen Platz setzen.
                setEnergy(energy / 2); // Split Energie.

                cell.decrementEnergy(energy);
            }
            else {
                // Clear this cell.
                setCellType(CellType.EMPTY);
                setEnergy(0);
            }
        }
    }

    /**
     * <ol>
     * <li>Findet ein Hai keinen Fisch auf einem angrenzenden Feld, so schwimmt er zufällig auf eines der vier Felder.</li>
     * <li>Für jeden Zyklus, während dessen der Hai keinen Fisch findet, verliert er einen Energiepunkt.</li>
     * <li>Findet der Hai einen Fisch, wird seine Energie um den Energiewert des Fisches erhöht.</li>
     * <li>Übersteigt die Energie den Wert für die Erzeugung eines Nachkommen ("Breed Energy"), so wird ein neuer Hai auf einem angrenzenden freien Feld
     * geboren. Die vorhandene Energie wird gleichmässig zwischen Eltern- und Kind-Hai verteilt.</li>
     * </ol>
     */
    private void nextShark() {
        ermittleNachbarn();

        decrementEnergy();

        // Try to eat.
        WaTorCell shark = this;
        final WaTorCell fisch = getFischNachbar();
        final int[] frei = getFreierNachbar();

        if (fisch != null || frei != null) {
            if (fisch != null) {
                shark = fisch;

                // Fish becomes a shark.
                shark.setCellType(CellType.SHARK);
                shark.incrementEnergy(energy); // Add the energy from the fish and the shark.

                // Clear this cell.
                setCellType(CellType.EMPTY);
                setEnergy(0);
            }
            else {
                // Do not eat, then move.
                final int freiX = frei[0];
                final int freiY = frei[1];

                shark = getSimulation().getCell(freiX, freiY);
                shark.setCellType(CellType.SHARK);
                shark.setEnergy(energy);

                // Clear this cell.
                setCellType(CellType.EMPTY);
                setEnergy(0);
            }

            if (shark.energy >= getSimulation().getSharkBreedEnergy()) {
                // Nachwuchs auf diesen Platz setzen.
                setCellType(CellType.SHARK);
                setEnergy(shark.energy / 2); // Split Energie.

                shark.decrementEnergy(energy);
            }
        }

        if (shark.energy <= getSimulation().getSharkStarveEnergy()) {
            // Dying.
            shark.setCellType(CellType.EMPTY);
            shark.setEnergy(0);
        }
    }
}
