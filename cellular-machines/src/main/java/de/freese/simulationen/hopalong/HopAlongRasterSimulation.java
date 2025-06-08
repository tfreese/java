// Created: 13.09.2009
package de.freese.simulationen.hopalong;

import java.awt.Color;
import java.awt.Point;

import de.freese.simulationen.model.AbstractRasterSimulation;
import de.freese.simulationen.model.EmptyCell;

/**
 * Model für die "Hop along"-Simulation.<br>
 * <a href="http://www.mathematische-basteleien.de/huepfer.htm">mathematische-basteleien</a>
 *
 * @author Thomas Freese
 */
public class HopAlongRasterSimulation extends AbstractRasterSimulation {
    private final Point center;

    private double x;
    private double y;

    public HopAlongRasterSimulation(final int width, final int height) {
        super(width, height);

        center = new Point(width / 2, height / 2);

        fillRaster(() -> new EmptyCell(this));
        reset();
    }

    @Override
    public void nextGeneration() {
        // INPUT num
        // INPUT a, b, c
        // x = 0
        // y = 0
        // PLOT(x, y)
        // FOR i = 1 TO num
        // xx = y - SIGN(x) * [ABS(b*x - c)]^0.5
        // yy = a - x
        // x = xx
        // y = yy

        // final double a = 0.01D;
        // final double b = -0.3D;
        // final double c = 0.003D;
        final double a = -14D;
        final double b = 0.9F;
        final double c = 0.1D;

        final double xx = y - (Math.signum(x) * Math.sqrt(Math.abs((b * x) - c)));
        // final double xx = y - (FastMath.signum(x) * FastMath.pow(Math.abs((b * x) - c), 0.5D));
        // final double xx = y + (FastMath.signum(x) * Math.abs((b * x) - c));
        final double yy = a - x;

        x = xx;
        y = yy;

        // double a = 80.0D;
        // x = (x * Math.cos(a)) + (((x * x) - y) * Math.sin(a));
        // y = (x * Math.sin(a)) - (((x * x) - y) * Math.cos(a));

        // Gingerbreadman
        // x = (1 - y) + Math.abs(x);
        // y = x;
        //
        // int newX = x + center.x;
        // int newY = y + center.y;
        //

        // if (Double.isNaN(xx) || Double.isNaN(yy)) {
        // return;
        // }

        // int newX = (int) x + center.x;
        // int newY = (int) y + center.y;
        //
        // if ((newX < 0) || (newY < 0)) {
        // return;
        // }
        //
        // if ((newX >= getWidth()) || (newY >= getHeight())) {
        // return;
        // }

        int newX = (int) x;
        int newY = (int) y;

        if (newX < 0) {
            newX = getXTorusCoord(0, newX);
            // return;
        }
        else if (newX >= getWidth()) {
            newX = getXTorusCoord(getWidth(), getWidth() - newX);
            // return;
        }

        if (newY < 0) {
            newY = getYTorusCoord(0, newY);
            // return;
        }
        else if (newX >= getWidth()) {
            newY = getYTorusCoord(getHeight(), getHeight() - newY);
            // return;
        }

        final EmptyCell cell = getCell(newX, newY);
        cell.setColor(Color.BLACK);

        fireCompleted();
    }

    @Override
    public void reset() {
        x = center.x;
        y = center.y;
        // // x = 100.1D;
        // // y = 100.1D;
        // // x = 1d;
        // // y = 1d;

        super.reset();
    }

    @Override
    protected EmptyCell getCell(final int x, final int y) {
        return (EmptyCell) super.getCell(x, y);
    }

    @Override
    protected void reset(final int x, final int y) {
        getCell(x, y).setColor(Color.WHITE);
    }
}
