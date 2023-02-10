// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.projection;

import de.freese.openstreetmap.model.coordinates.EastNorth;
import de.freese.openstreetmap.model.coordinates.LatLon;

/**
 * Directly use latitude / longitude values as x/y.
 *
 * @author Thomas Freese
 */
public class Epsg4326 implements Projection {
    /**
     * 360 degrees.
     */
    private static final int DEGREES360 = 360;

    /**
     * @see de.freese.openstreetmap.model.projection.Projection#eastNorth2LatLon(de.freese.openstreetmap.model.coordinates.EastNorth)
     */
    @Override
    public LatLon eastNorth2LatLon(final EastNorth p) {
        return new LatLon(p.north(), p.east());
    }

    /**
     * @see de.freese.openstreetmap.model.projection.Projection#latLon2EastNorth(double, double)
     */
    @Override
    public EastNorth latLon2EastNorth(final double lat, final double lon) {
        return new EastNorth(lon, lat);
    }

    /**
     * @see de.freese.openstreetmap.model.projection.Projection#latLon2EastNorth(de.freese.openstreetmap.model.coordinates.LatLon)
     */
    @Override
    public EastNorth latLon2EastNorth(final LatLon pLatLon) {
        return latLon2EastNorth(pLatLon.lat(), pLatLon.lon());
    }

    /**
     * 1/360
     *
     * @see de.freese.openstreetmap.model.projection.Projection#scaleFactor()
     */
    @Override
    public double scaleFactor() {
        return 1.0 / DEGREES360;
    }

    /**
     * "EPSG:4326"
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EPSG:4326";
    }
}
