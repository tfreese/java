// Created: 27.12.2021
package de.freese.simulationen.factal;

import java.awt.Color;

import de.freese.simulationen.model.AbstractCell;

/**
 * @author Thomas Freese
 */
public class FractalCell extends AbstractCell {
    static final int ITERATIONEN = 50;
    private static final double EPSILON = 0.0001D;

    private static boolean equal(final double xi, final double x, final double yi, final double y) {
        return fabs(x - y) < EPSILON && fabs(xi - yi) < EPSILON;
    }

    private static double fabs(final double a) {
        if (a < 0) {
            return -a;
        }

        return a;
    }

    public FractalCell(final FractalRasterSimulation simulation) {
        super(simulation, Color.BLACK);
    }

    public int checkConvergence(final double ci, final double cr) {
        // Startwerte sind veränderbar.
        double zi = 0.0D;
        double z = 0.0D;

        double ziLast = 0D;
        double zLast = 999D;

        for (int i = 0; i < ITERATIONEN; i++) {
            if (equal(zi, z, ziLast, zLast)) {
                return i;
            }

            ziLast = zi;
            zLast = z;

            final double ziT = 2 * (z * zi);
            final double zT = (z * z) - (zi * zi);

            z = zT + cr;
            zi = ziT + ci;
        }

        return ITERATIONEN;
    }

    public int checkFast(final double ci, final double cr) {
        // Startwerte sind veränderbar.
        double zi = 0.0D;
        double z = 0.0D;

        for (int i = 0; i < ITERATIONEN; i++) {
            final double ziT = 2 * (z * zi);
            final double zT = (z * z) - (zi * zi);

            z = zT + cr;
            zi = ziT + ci;

            if (((z * z) + (zi * zi)) >= 4.0) {
                return i;
            }
        }

        return ITERATIONEN;
    }

    @Override
    public void nextGeneration() {
        // Empty
    }
}
