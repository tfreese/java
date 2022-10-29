// Created: 11.03.2021
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.freese.simulationen.model.AbstractCell;

/**
 * @author Thomas Freese
 */
public class WaTorCell extends AbstractCell
{
    /**
     * @author Thomas Freese
     */
    public enum CellType
    {
        /**
         *
         */
        EMPTY,
        /**
         *
         */
        FISH,
        /**
         *
         */
        SHARK
    }

    /**
     *
     */
    private CellType cellType;
    /**
     *
     */
    private int energy;
    /**
     *
     */
    private final List<int[]> fischNachbarnList = new ArrayList<>(8);
    /**
     *
     */
    private final List<int[]> freieNachbarnList = new ArrayList<>(8);

    /**
     * Erstellt ein neues {@link WaTorCell} Object.
     *
     * @param simulation {@link WaTorRasterSimulation}
     */
    public WaTorCell(final WaTorRasterSimulation simulation)
    {
        super(simulation);
    }

    /**
     * Erniedrigt den Energiewert um 1.
     */
    private void decrementEnergy()
    {
        this.energy--;
    }

    /**
     * Erniedrigt den Energiewert.
     *
     * @param delta int
     */
    private void decrementEnergy(final int delta)
    {
        this.energy -= delta;
    }

    /**
     * Ermittelt die Nachbarn dieser Zelle.<br>
     */
    private void ermittleNachbarn()
    {
        this.freieNachbarnList.clear();
        this.fischNachbarnList.clear();

        visitNeighbours((x, y) ->
        {
            WaTorCell cell = getSimulation().getCell(x, y);

            if (cell.isEmpty())
            {
                this.freieNachbarnList.add(new int[]
                        {
                                x, y
                        });
            }
            else if (cell.isFish())
            {
                this.fischNachbarnList.add(new int[]
                        {
                                x, y
                        });
            }
        });
    }

    /**
     * Liefert einen Fisch in der Nachbarschaft oder keinen.
     *
     * @return {@link WaTorCell}
     */
    protected WaTorCell getFischNachbar()
    {
        if (this.fischNachbarnList.isEmpty())
        {
            return null;
        }

        while (!this.fischNachbarnList.isEmpty())
        {
            final int size = this.fischNachbarnList.size();
            final int[] koords = this.fischNachbarnList.remove(getSimulation().getRandom().nextInt(size));

            final WaTorCell cell = getSimulation().getCell(koords[0], koords[1]);

            // Ist das wirklich noch ein Fisch ?
            if (cell.isFish())
            {
                return cell;
            }
        }

        return null;
    }

    /**
     * Liefert die Koordinaten einer freien Zelle in der Nachbarschaft oder keine.
     *
     * @return int[]
     */
    private int[] getFreierNachbar()
    {
        if (this.freieNachbarnList.isEmpty())
        {
            return null;
        }

        while (!this.freieNachbarnList.isEmpty())
        {
            final int size = this.freieNachbarnList.size();
            int[] koords = this.freieNachbarnList.remove(getSimulation().getRandom().nextInt(size));

            final WaTorCell cell = getSimulation().getCell(koords[0], koords[1]);

            // Ist die Stelle wirklich noch frei ?
            if (cell.isEmpty())
            {
                return koords;
            }
        }

        return null;
    }

    /**
     * @see de.freese.simulationen.model.AbstractCell#getSimulation()
     */
    @Override
    protected WaTorRasterSimulation getSimulation()
    {
        return (WaTorRasterSimulation) super.getSimulation();
    }

    /**
     * Erhoeht den Energiewert um 1.
     */
    void incrementEnergy()
    {
        this.energy++;
    }

    /**
     * Erhoeht den Energiewert.
     *
     * @param delta int
     */
    private void incrementEnergy(final int delta)
    {
        this.energy += delta;
    }

    /**
     * @return boolean
     */
    public boolean isEmpty()
    {
        return CellType.EMPTY.equals(this.cellType);
    }

    /**
     * @return boolean
     */
    public boolean isFish()
    {
        return CellType.FISH.equals(this.cellType);
    }

    /**
     * @return boolean
     */
    public boolean isShark()
    {
        return CellType.SHARK.equals(this.cellType);
    }

    /**
     * <ol>
     * <li>Jeder Fisch schwimmt zufällig auf eines der vier angrenzenden Felder, sofern es leer ist.
     * <li>Mit jedem Durchgang gewinnt der Fisch einen Energiepunkt.
     * <li>Übersteigt die Energie den Wert für die Erzeugung eines Nachkommen ("Breed Energy"), so wird ein neuer Fisch auf einem angrenzenden freien Feld
     * geboren. Die vorhandene Energie wird gleichmässig zwischen Eltern- und Kind-Fisch verteilt.
     * </ol>
     */
    private void nextFish()
    {
        ermittleNachbarn();

        incrementEnergy();

        final int[] frei = getFreierNachbar();

        if (frei != null)
        {
            final int freiX = frei[0];
            final int freiY = frei[1];

            // Fisch "bewegen".
            final WaTorCell cell = getSimulation().getCell(freiX, freiY);
            cell.setCellType(CellType.FISH);
            cell.setEnergy(this.energy);

            if (this.energy >= getSimulation().getFishBreedEnergy())
            {
                // Nachwuchs auf diesen Platz setzen.
                setEnergy(this.energy / 2); // Energie aufteilen

                cell.decrementEnergy(this.energy);
            }
            else
            {
                // Diese Zelle leeren.
                setCellType(CellType.EMPTY);
                setEnergy(0);
            }
        }
    }

    /**
     * @see de.freese.simulationen.model.Cell#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        if (CellType.FISH.equals(this.cellType))
        {
            nextFish();
        }
        else if (CellType.SHARK.equals(this.cellType))
        {
            nextShark();
        }
    }

    /**
     * <ol>
     * <li>Findet ein Hai keinen Fisch auf einem angrenzenden Feld, so schwimmt er zufällig auf eines der vier Felder.
     * <li>Für jeden Zyklus, während dessen der Hai keinen Fisch findet, verliert er einen Energiepunkt.
     * <li>Findet der Hai einen Fisch, wird seine Energie um den Energiewert des Fisches erhöht.
     * <li>Übersteigt die Energie den Wert für die Erzeugung eines Nachkommen ("Breed Energy"), so wird ein neuer Hai auf einem angrenzenden freien Feld
     * geboren. Die vorhandene Energie wird gleichmässig zwischen Eltern- und Kind-Hai verteilt.
     * </ol>
     */
    private void nextShark()
    {
        ermittleNachbarn();

        decrementEnergy();

        // Versuchen zu fressen.
        WaTorCell shark = this;
        final WaTorCell fisch = getFischNachbar();
        final int[] frei = getFreierNachbar();

        if ((fisch != null) || (frei != null))
        {
            if (fisch != null)
            {
                shark = fisch;

                // Der Fisch wird zum Hai.
                shark.setCellType(CellType.SHARK);
                shark.incrementEnergy(this.energy); // Energy von Fisch und Hai addieren.

                // Diese Zelle leeren.
                setCellType(CellType.EMPTY);
                setEnergy(0);
            }
            else
            {
                // Nicht gefressen, dann bewegen.
                final int freiX = frei[0];
                final int freiY = frei[1];

                shark = getSimulation().getCell(freiX, freiY);
                shark.setCellType(CellType.SHARK);
                shark.setEnergy(this.energy);

                // Diese Zelle leeren.
                setCellType(CellType.EMPTY);
                setEnergy(0);
            }

            if (shark.energy >= getSimulation().getSharkBreedEnergy())
            {
                // Nachwuchs auf diesen Platz setzen.
                setCellType(CellType.SHARK);
                setEnergy(shark.energy / 2); // Energie aufteilen

                shark.decrementEnergy(this.energy);
            }
        }

        if (shark.energy <= getSimulation().getSharkStarveEnergy())
        {
            // Sterben
            shark.setCellType(CellType.EMPTY);
            shark.setEnergy(0);
        }
    }

    /**
     * @param cellType {@link CellType}
     */
    public void setCellType(final CellType cellType)
    {
        this.cellType = cellType;

        switch (cellType)
        {
            case FISH -> setColor(Color.GREEN);
            case SHARK -> setColor(Color.BLUE);

            default -> setColor(Color.BLACK);
        }
    }

    /**
     * @param energy int
     */
    public void setEnergy(final int energy)
    {
        this.energy = energy;
    }
}
