package de.freese.openstreetmap;

/**
 * Konvertiert die Längen- und Breitengerade in kartesische XY Koordinaten.<br>
 * Basis ist der "Elliptical Mercator".<br>
 * Quelle: <a href="http://wiki.openstreetmap.org/wiki/Mercator">Mercator</a><br>
 * Quelle: <a href="http://de.wikipedia.org/wiki/Mercator-Projektion">Mercator-Projektion</a>
 *
 * @author Thomas Freese
 */
public final class Mercator
{
    /**
     * Breitengrad.<br>
     */
    public static double mercX(final double longitude)
    {
        double x;

        // Formel nach OpenStreetMap.
        // x = longitude * RAD_AEQUATOR_RADIUS;

        // Formel nach Wiki.
        x = longitude * MercatorConstants.RAD_ERD_UMFANG;

        return x;
    }

    /**
     * Längengrad.<br>
     */
    public static double mercY(final double latitude)
    {
        double lat = latitude;

        if (lat > 89.5D)
        {
            lat = 89.5D;
        }
        if (lat < -89.5D)
        {
            lat = -89.5D;
        }

        double y;

        // Formel nach OpenStreetMap.
        // double phi = m_lat * RAD;
        // double sinphi = Math.sin(phi);
        // double con = EXZENTRIZITAET * sinphi;
        // con = Math.pow(((1.0D - con) / (1.0D + con)), EXZENTRIZITAET_HALBE);
        // double ts = Math.tan(0.5D * (PI_HALBE - phi)) / con;
        // y = 0 - (AEQUATOR_RADIUS * Math.log(ts));

        // Formel nach Wiki.
        y = Math.log(Math.tan(MercatorConstants.PI_VIERTEL + (lat * MercatorConstants.RAD_HALBE))) * MercatorConstants.ERD_UMFANG;

        return y;
    }

    private Mercator()
    {
        super();
    }
}
