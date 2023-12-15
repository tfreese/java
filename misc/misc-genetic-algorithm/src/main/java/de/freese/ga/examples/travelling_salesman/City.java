// Created: 26.08.2015
package de.freese.ga.examples.travelling_salesman;

/**
 * Genom Value
 *
 * @author Thomas Freese
 */
public record City(String name, int x, int y) {
    public double distanceTo(final City city) {
        final double xDistance = Math.abs(x() - city.x());
        final double yDistance = Math.abs(y() - city.y());

        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name());
        sb.append("(").append(x()).append(":").append(y()).append(")");

        return sb.toString();
    }
}
