// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.coordinates;

/**
 * Base class of points of both coordinate system. The variables are default package protected to allow routines in the data package to access them directly. As
 * the class itself is package protected too, it is not visible outside the data package. Routines there should only use LatLon or EastNorth
 *
 * @author imi
 * @author Thomas Freese
 */
public abstract class Coordinate {
    /**
     * Return the squared distance of the northing/easting values between this and the argument.
     *
     * @param latA This point to calculate the distance from.
     * @param lonA This point to calculate the distance from.
     * @param latB The other point to calculate the distance to.
     * @param lonB The other point to calculate the distance to.
     *
     * @return The square of the distance between this and the other point, regarding the x/y values.
     */
    public static double distance(final double latA, final double lonA, final double latB, final double lonB) {
        return ((latA - latB) * (latA - latB)) + ((lonA - lonB) * (lonA - lonB));
    }

    /**
     * Either easting or latitude.
     */
    private final double myXCoord;
    /**
     * Either northing or longitude.
     */
    private final double myYCoord;

    /**
     * Construct the point with latitude / longitude values. The x/y values are left uninitialized.
     *
     * @param px Latitude of the point.
     * @param py Longitude of the point.
     */
    Coordinate(final double px, final double py) {
        super();

        myXCoord = px;
        myYCoord = py;
    }

    /**
     * Return the squared distance of the northing/easting values between this and the argument.
     *
     * @param other The other point to calculate the distance to.
     *
     * @return The square of the distance between this and the other point, regarding the x/y values.
     */
    public double distance(final Coordinate other) {
        return ((myXCoord - other.myXCoord) * (myXCoord - other.myXCoord)) + ((myYCoord - other.myYCoord) * (myYCoord - other.myYCoord));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Coordinate other)) {
            return false;
        }

        if (Double.doubleToLongBits(myXCoord) != Double.doubleToLongBits(other.myXCoord)) {
            return false;
        }

        return Double.doubleToLongBits(myYCoord) == Double.doubleToLongBits(other.myYCoord);
    }

    /**
     * @return Either easting or latitude
     */
    public double getXCoord() {
        return myXCoord;
    }

    /**
     * @return Either northing or longitude
     */
    public double getYCoord() {
        return myYCoord;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        long temp = Double.doubleToLongBits(myXCoord);
        result = (prime * result) + Long.hashCode(temp);

        temp = Double.doubleToLongBits(myYCoord);
        result = (prime * result) + Long.hashCode(temp);

        return result;
    }
}
