// Created: 28.09.2009
package de.freese.simulationen.model;

import java.awt.Color;
import java.util.function.BiConsumer;

/**
 * Basisklasse einer Zelle.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCell implements Cell {
    private final AbstractRasterSimulation simulation;

    private Color color;

    private int x = -1;

    private int y = -1;

    protected AbstractCell(final AbstractRasterSimulation simulation) {
        super();

        this.simulation = simulation;
    }

    protected AbstractCell(final AbstractRasterSimulation simulation, final Color color) {
        super();

        this.simulation = simulation;
        this.color = color;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    public void setColor(final Color color) {
        this.color = color;

        getSimulation().setCellColor(getX(), getY(), color);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" ");
        sb.append("[x=").append(getX()).append(",y=").append(getY()).append("]");

        return sb.toString();
    }

    void setXY(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    protected AbstractRasterSimulation getSimulation() {
        return this.simulation;
    }

    /**
     * Liefert nur die Nord-, Ost-, Süd- und West-Nachbarn dieser Zelle.
     */
    protected void visitNeighbours(final BiConsumer<Integer, Integer> biConsumer) {
        int xWest = getSimulation().getXTorusKoord(getX(), -1);
        int xOst = getSimulation().getXTorusKoord(getX(), +1);
        int ySued = getSimulation().getYTorusKoord(getY(), -1);
        int yNord = getSimulation().getYTorusKoord(getY(), +1);

        // Nord
        biConsumer.accept(getX(), yNord);

        // Ost
        biConsumer.accept(xOst, getY());

        // Süd
        biConsumer.accept(getX(), ySued);

        // West
        biConsumer.accept(xWest, getY());
    }

    /**
     * Liefert alle Nachbarn dieser Zelle.
     */
    protected void visitNeighboursAll(final BiConsumer<Integer, Integer> biConsumer) {
        int xWest = getSimulation().getXTorusKoord(getX(), -1);
        int xOst = getSimulation().getXTorusKoord(getX(), +1);
        int ySued = getSimulation().getYTorusKoord(getY(), -1);
        int yNord = getSimulation().getYTorusKoord(getY(), +1);

        // Nord
        biConsumer.accept(getX(), yNord);

        // Nord-Ost
        biConsumer.accept(xOst, yNord);

        // Ost
        biConsumer.accept(xOst, getY());

        // Süd-Ost
        biConsumer.accept(xOst, ySued);

        // Süd
        biConsumer.accept(getX(), ySued);

        // Süd-West
        biConsumer.accept(xWest, ySued);

        // West
        biConsumer.accept(xWest, getY());

        // Nord-West
        biConsumer.accept(xWest, yNord);
    }
}
