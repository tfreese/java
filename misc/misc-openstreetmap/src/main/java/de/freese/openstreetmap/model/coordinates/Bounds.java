// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.coordinates;

import de.freese.openstreetmap.model.projection.Projection;

/**
 * This is a simple data class for "rectangular" areas of the world, given in lat/lon min/max values. Do not confuse this with "Area", which is an OSM-primitive
 * for a vector of nodes, describing some area (like a sea).
 *
 * @author imi
 * @author Thomas Freese
 */
public class Bounds {
    /**
     * The maximum bounds possible.
     */
    public static final Bounds WORLD = new Bounds();
    /**
     * The minimum and maximum coordinates.
     */
    private LatLon myMax;
    /**
     * The minimum and maximum coordinates.
     */
    private LatLon myMin;

    public Bounds() {
        super();

        myMin = new LatLon(-Projection.MAX_LAT, -Projection.MAX_LON);
        myMax = new LatLon(Projection.MAX_LAT, Projection.MAX_LON);
    }

    public Bounds(final double lat0, final double lon0, final double lat1, final double lon1) {
        super();

        myMin = new LatLon(Math.min(lat0, lat1), Math.min(lon0, lon1));
        myMax = new LatLon(Math.max(lat0, lat1), Math.max(lon0, lon1));
    }

    public Bounds(final LatLon center, final double radius) {
        this(center.lat() - radius, center.lon() - radius, center.lat() + radius, center.lon() + radius);
    }

    public Bounds(final LatLon min, final LatLon max) {
        this(min.lat(), min.lon(), max.lat(), max.lon());
    }

    public LatLon center() {
        // not sure, whether this calculation is right... maybe there is some
        // more complex calculation needed to get a center of a spherical
        // dimension?
        return new LatLon((myMin.lat() + myMax.lat()) / 2, (myMin.lon() + myMax.lon()) / 2);
    }

    /**
     * @return true if the given coordinates are within this bounding-box
     */
    public boolean contains(final double latitude, final double longitude) {
        // return !(latitude < myMin.lat()) && !(latitude > myMax.lat()) && !(longitude < myMin.lon()) && !(longitude > myMax.lon());
        return latitude >= myMin.lat() && latitude <= myMax.lat() && longitude >= myMin.lon() && longitude <= myMax.lon();
    }

    // /**
    // * Extend the bounds if necessary to include the given point.
    // */
    // public void extend(double LatLon ll) {
    // if (ll.lat() < min.lat()
    // || ll.lon() < min.lon()) {
    // min = new LatLon(Math.min(ll.lat(), min.lat()), Math.min(ll.lon(), min.lon()));
    // }
    // if (ll.lat() > max.lat()
    // || ll.lon() > max.lon()) {
    // max = new LatLon(Math.max(ll.lat(), max.lat()), Math.max(ll.lon(), max.lon()));
    // }
    // }

    public LatLon getCenter() {
        final double centerLat = getMin().lat() + ((getMax().lat() - getMin().lat()) / 2.0);
        final double centerLon = getMin().lon() + ((getMax().lon() - getMin().lon()) / 2.0);

        return new LatLon(centerLat, centerLon);
    }

    public LatLon getMax() {
        return myMax;
    }

    public LatLon getMin() {
        return myMin;
    }

    /**
     * @return the maximum of the coordinate-distances of the max and min lat and lon-values.
     */
    public double getSize() {
        return Math.max(Math.abs(getMax().lat() - getMin().lat()), getMax().lon() - getMin().lon());
    }

    public void setMax(final LatLon aMax) {
        this.myMax = aMax;
    }

    public void setMin(final LatLon aMin) {
        this.myMin = aMin;
    }

    @Override
    public String toString() {
        return "Bounds[" + myMin.lat() + "," + myMin.lon() + "," + myMax.lat() + "," + myMax.lon() + "]";
    }
}
