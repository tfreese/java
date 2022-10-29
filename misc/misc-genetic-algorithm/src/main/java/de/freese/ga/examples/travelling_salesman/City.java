// Created: 26.08.2015
package de.freese.ga.examples.travelling_salesman;

/**
 * Genom Value
 *
 * @param name
 * @param x
 * @param y
 *
 * @author Thomas Freese
 */
public record City(String name, int x, int y)
{
    /**
     * @param city {@link City}
     *
     * @return double
     */
    public double distanceTo(final City city)
    {
        double xDistance = Math.abs(x() - city.x());
        double yDistance = Math.abs(y() - city.y());

        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name());
        sb.append("(").append(x()).append(":").append(y()).append(")");

        return sb.toString();
    }
}
