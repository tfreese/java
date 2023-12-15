// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.coordinates;

import de.freese.openstreetmap.model.projection.Projection;

/**
 * LatLon are unprojected latitude / longitude coordinates.<br>
 * This class is immutable.<br>
 * From Aviation Formulary v1.3. <a href="http://williams.best.vwh.net/avform.htm">Aviation Formulary</a>.
 *
 * @author Imi
 * @author Thomas Freese
 */
public class LatLon extends Coordinate {
    /**
     * @param lat1 position1
     * @param lon1 position1
     * @param lat2 position2
     * @param lon2 position2
     *
     * @return the course from position1 to position2
     */
    public static double course(final double lat1, final double lon1, final double lat2, final double lon2) {
        return Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), (Math.cos(lat1) * Math.sin(lat2)) - (Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2))) % (2 * Math.PI);
    }

    /**
     * Calculate course from dLat and dLon. dLat = lat2 - lat1; dLon = lon2 - lon1.
     *
     * @param lat1 latitude of first point
     * @param lon1 longitude of first point
     * @param lat2 latitude of second point
     * @param lon2 longitude of second point
     *
     * @return the course (direction) in degrees
     */
    public static double deriveCourse(final double lat1, final double lon1, final double lat2, final double lon2) {
        final double dLat = lat1 - lat2;
        final double dLon = lon1 - lon2;
        final double alpha = (Math.atan2(dLat, dLon) * 180D) / Math.PI;

        if (alpha <= 90) {
            return 90 - alpha;
        }

        return 450 - alpha;
    }

    /**
     * Distance between 2 points of a sphere.
     *
     * @param lat1 position1
     * @param lon1 position1
     * @param lat2 position2
     * @param lon2 position2
     *
     * @return the great-circle-distance
     */
    public static double dist(final double lat1, final double lon1, final double lat2, final double lon2) {
        return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2), 2) + (Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lon1 - lon2) / 2), 2))));
    }

    /**
     * Computes the distance between this lat/lon and another point on the earth.<br>
     * Uses spherical law of cosines formula, not Haversine.<br>
     * Earth is ellipsoid.
     *
     * @param lat1 the first point.
     * @param lon1 the first point.
     * @param lat2 the second point.
     * @param lon2 the second point.
     *
     * @return distance in metres.
     */
    public static int distanceInMeters(final double lat1, final double lon1, final double lat2, final double lon2) {
        final int factor = (int) ((6378 - (21 * Math.sin(Math.toRadians((lat1 + lat2) / 2)))) * 1000);
        // final int factor = 6371000;
        final double p1lat = Math.toRadians(lat1);
        final double p2lat = Math.toRadians(lat2);

        return (int) (Math.acos((Math.sin(p1lat) * Math.sin(p2lat)) + (Math.cos(p1lat) * Math.cos(p2lat) * Math.cos(Math.toRadians(lon2 - lon1)))) * factor);
    }

    /**
     * Computes the distance between this lat/lon and another point on the earth.<br>
     * Uses spherical law of cosines formula, not Haversine.<br>
     * Earth is ellipsoid..
     *
     * @param point1 the first point.
     * @param point2 the second point.
     *
     * @return distance in metres.
     */
    public static int distanceInMeters(final LatLon point1, final LatLon point2) {
        return distanceInMeters(point1.lat(), point1.lon(), point2.lat(), point2.lon());
        // final int factor = (int) ((6378 - 21 * Math.sin(Math.toRadians((point1.lat() +
        // point2.lat()) / 2))) * 1000);
        // //final int factor = 6371000;
        // final double p1lat = Math.toRadians(point1.lat());
        // final double p2lat = Math.toRadians(point2.lat());
        // return (int) (
        // Math.acos(
        // Math.sin(p1lat) * Math.sin(p2lat) + Math.cos(p1lat) * Math.cos(p2lat)
        // * Math.cos(Math.toRadians(point2.lon() - point1.lon()))
        // ) * factor);
    }

    /**
     * Given a squared distance as returned by {@link #distance(Coordinate)}, calculate the approximate distance in kilometers.
     *
     * @param aDist the squared distance in northing/easting-space
     * @param projection {@link Projection}
     *
     * @return the approximate distance in Km.
     */
    public static double distanceToKilometers(final double aDist, final Projection projection) {
        final int kilo = 1000;

        return (Math.sqrt(aDist) * projection.scaleFactor() * Projection.EARTH_CIRCUMFENCE_IN_METERS) / kilo;
    }

    /**
     * @param lat1 position1
     * @param lon1 position1
     * @param lat2 position2
     * @param lon2 position2
     * @param lat3 position3 (where we are)
     * @param lon3 position3 (where we are)
     *
     * @return the course-track-error.
     */
    public static double xtd(final double lat1, final double lon1, final double lat2, final double lon2, final double lat3, final double lon3) {
        final double distAD = dist(lat1, lon1, lat3, lon3);
        final double crsAD = course(lat1, lon1, lat3, lon3);
        final double crsAB = course(lat1, lon1, lat2, lon2);

        return Math.asin(Math.sin(distAD) * Math.sin(crsAD - crsAB));
    }

    /**
     * @param lat unprojected latitude
     * @param lon unprojected longitude
     */
    public LatLon(final double lat, final double lon) {
        super(lat, lon);
    }

    /**
     * @param other the LatLon to compare against
     *
     * @return <code>true</code>, if the other point has almost the same lat/lon values, only differ by no more than 1/Projection.MAX_SERVER_PRECISION.
     */
    public boolean equalsEpsilon(final LatLon other) {
        final double p = 1 / Projection.MAX_SERVER_PRECISION;

        return (Math.abs(lat() - other.lat()) <= p) && (Math.abs(lon() - other.lon()) <= p);
    }

    /**
     * @return <code>true</code>, if the coordinate is outside the world, compared by using lat/lon.
     */
    public boolean isOutSideWorld() {
        return (lat() < -Projection.MAX_LAT) || (lat() > Projection.MAX_LAT) || (lon() < -Projection.MAX_LON) || (lon() > Projection.MAX_LON);
    }

    /**
     * @param b the bounds to check against
     *
     * @return <code>true</code> if this is within the given bounding box.
     */
    public boolean isWithin(final Bounds b) {
        return (lat() >= b.getMin().lat()) && (lat() <= b.getMax().lat()) && (lon() > b.getMin().lon()) && (lon() < b.getMax().lon());
    }

    /**
     * @return the unprojected latitude
     */
    public double lat() {
        return super.getXCoord();
    }

    /**
     * @return the unprojected longitude
     */
    public double lon() {
        return super.getYCoord();
    }

    @Override
    public String toString() {
        return "LatLon[lat=" + lat() + ",lon=" + lon() + "]";
    }
}
