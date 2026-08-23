package de.freese.ga.examples.travelling_salesman;

/**
 * Genom Value
 *
 * @author Thomas Freese
 * @since 26.08.2015
 */
public record City(String name, int x, int y) {
    public double distanceTo(final City city) {
        final double xDistance = Math.abs(x() - city.x());
        final double yDistance = Math.abs(y() - city.y());

        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }

    @Override
    public String toString() {
        return name() + "(" + x() + ":" + y() + ")";
    }
}
