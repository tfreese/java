// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.projection;

import de.freese.openstreetmap.model.coordinates.EastNorth;
import de.freese.openstreetmap.model.coordinates.LatLon;

/**
 * Implement Mercator Projection code, coded after documentation from wikipedia.<br>
 * The center of the mercator projection is always the 0 grad coordinate.
 *
 * @author Thomas Freese
 */
public class Mercator implements Projection {
    /**
     * 180 Grad.
     */
    private static final int C_180 = 180;
    /**
     * 360 Grad.
     */
    private static final int C_360 = 360;
    /**
     * 1/4 of Pi.
     */
    private static final double QUARTERPI = Math.PI / (2 + 2);

    /**
     * @see de.freese.openstreetmap.model.projection.Projection#eastNorth2LatLon(de.freese.openstreetmap.model.coordinates.EastNorth)
     */
    @Override
    public LatLon eastNorth2LatLon(final EastNorth p) {
        return new LatLon((Math.atan(Math.sinh(p.north())) * C_180) / Math.PI, (p.east() * C_180) / Math.PI);
    }

    /**
     * @see de.freese.openstreetmap.model.projection.Projection#latLon2EastNorth(double, double)
     */
    @Override
    public EastNorth latLon2EastNorth(final double lat, final double lon) {
        return new EastNorth((lon * Math.PI) / C_180, Math.log(Math.tan(QUARTERPI + ((lat * Math.PI) / C_360))));
    }

    /**
     * @see de.freese.openstreetmap.model.projection.Projection#latLon2EastNorth(de.freese.openstreetmap.model.coordinates.LatLon)
     */
    @Override
    public EastNorth latLon2EastNorth(final LatLon pLatLon) {
        return latLon2EastNorth(pLatLon.lat(), pLatLon.lon());
    }

    /**
     * 1/(pi/2)
     *
     * @see de.freese.openstreetmap.model.projection.Projection#scaleFactor()
     */
    @Override
    public double scaleFactor() {
        return 1 / Math.PI / 2;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Mercator";
    }
}
