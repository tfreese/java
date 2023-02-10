// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.projection;

import de.freese.openstreetmap.model.coordinates.EastNorth;
import de.freese.openstreetmap.model.coordinates.LatLon;

/**
 * Classes subclass are able to convert lat/lon values to planear screen coordinates.
 *
 * @author imi
 * @author Thomas Freese
 */
public interface Projection {
    /**
     * List of all available Projections.
     */
    Projection[] ALL_PRJECTIONS = {new Epsg4326(), new Mercator()};

    /**
     * circumference of the earth in meter.
     */
    long EARTH_CIRCUMFENCE_IN_METERS = 40_041_455L;

    /**
     * Mercator squares the world.
     */
    double MAX_LAT = 85.05112877980659;

    /**
     * The maximum possible longitude is 180� .
     */
    double MAX_LON = 180;

    /**
     * The minimal distance that 2 coordinates in OpenStreetMap can have without being the same.
     */
    double MAX_SERVER_PRECISION = 1e12;

    /**
     * Convert from norting/easting to lat/lon.
     *
     * @param p The geo point to convert. lat/lon members of the point are filled.
     *
     * @return the converted values
     */
    LatLon eastNorth2LatLon(EastNorth p);

    /**
     * Convert from lat/lon to northing/easting.
     *
     * @param lat The geo point to convert. x/y members of the point are filled.
     * @param lon The geo point to convert. x/y members of the point are filled.
     *
     * @return the converted values
     */
    EastNorth latLon2EastNorth(double lat, double lon);

    /**
     * Convert from lat/lon to northing/easting.
     *
     * @param latLon The geo point to convert. x/y members of the point are filled.
     *
     * @return the converted values
     */
    EastNorth latLon2EastNorth(LatLon latLon);

    /**
     * The factor to multiply with an easting coordinate to get from "easting units per pixel" to "meters per pixel".
     */
    double scaleFactor();
}
