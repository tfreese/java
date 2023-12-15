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

        this.center = new Point(width / 2, height / 2);

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

        final double xx = this.y - (Math.signum(this.x) * Math.sqrt(Math.abs((b * this.x) - c)));
        // final double xx = y - (FastMath.signum(x) * FastMath.pow(Math.abs((b * x) - c), 0.5D));
        // final double xx = this.y + (FastMath.signum(this.x) * Math.abs((b * this.x) - c));
        final double yy = a - this.x;

        this.x = xx;
        this.y = yy;

        // double a = 80.0D;
        // this.x = (this.x * Math.cos(a)) + (((this.x * this.x) - this.y) * Math.sin(a));
        // this.y = (this.x * Math.sin(a)) - (((this.x * this.x) - this.y) * Math.cos(a));

        // Gingerbreadman
        // this.x = (1 - this.y) + Math.abs(this.x);
        // this.y = this.x;
        //
        // int newX = this.x + this.center.x;
        // int newY = this.y + this.center.y;
        //

        // if (Double.isNaN(xx) || Double.isNaN(yy)) {
        // return;
        // }

        // int newX = (int) this.x + this.center.x;
        // int newY = (int) this.y + this.center.y;
        //
        // if ((newX < 0) || (newY < 0)) {
        // return;
        // }
        //
        // if ((newX >= getWidth()) || (newY >= getHeight())) {
        // return;
        // }

        int newX = (int) this.x;
        int newY = (int) this.y;

        if (newX < 0) {
            newX = getXTorusKoord(0, newX);
            // return;
        }
        else if (newX >= getWidth()) {
            newX = getXTorusKoord(getWidth(), getWidth() - newX);
            // return;
        }

        if (newY < 0) {
            newY = getYTorusKoord(0, newY);
            // return;
        }
        else if (newX >= getWidth()) {
            newY = getYTorusKoord(getHeight(), getHeight() - newY);
            // return;
        }

        final EmptyCell cell = getCell(newX, newY);
        cell.setColor(Color.BLACK);

        fireCompleted();
    }

    @Override
    public void reset() {
        this.x = this.center.x;
        this.y = this.center.y;
        // // this.x = 100.1D;
        // // this.y = 100.1D;
        // // this.x = 1d;
        // // this.y = 1d;

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
