// Created: 13.11.22
package de.freese.openstreetmap;

/**
 * @author Thomas Freese
 */
final class MercatorConstants
{
    /**
     * Maximaler Erdradius in m.
     */
    public static final double AEQUATOR_RADIUS = 6378137.0D;
    /**
     * Erdumfang in Meter.
     */
    public static final double ERD_UMFANG = 2.0D * getMittlererRadius() * Math.PI;
    /**
     * RAD * ERD_UMFANG
     */
    public static final double RAD_ERD_UMFANG = getRad() * ERD_UMFANG;
    /**
     * POLAR_RADIUS / AEQUATOR_RADIUS
     */
    public static final double FORMFAKTOR = getPolarRadius() / AEQUATOR_RADIUS;
    /**
     * 1.0D - (FORMFAKTOR * FORMFAKTOR)
     */
    public static final double ABPLATTUNG = 1.0D - (FORMFAKTOR * FORMFAKTOR);
    /**
     * Math.sqrt(ABPLATTUNG)
     */
    public static final double EXZENTRIZITAET = Math.sqrt(ABPLATTUNG);
    /**
     * EXZENTRIZITAET / 2.0D
     */
    public static final double EXZENTRIZITAET_HALBE = EXZENTRIZITAET / 2.0D;
    /**
     * Minimaler Erdradius in m.
     */
    public static final double MITTLERER_RADIUS = 6371000.8D;
    /**
     * Math.PI / 2.0D
     */
    public static final double PI_HALBE = Math.PI / 2.0D;
    /**
     * Math.PI / 4.0D
     */
    public static final double PI_VIERTEL = Math.PI / 4.0D;
    /**
     * Minimaler Erdradius in m.
     */
    public static final double POLAR_RADIUS = 6356752.3142D;
    /**
     * Rad = Winkel * (Math.PI / 180.0D) = Math.toRadians(Winkel)
     */
    public static final double RAD = Math.PI / 180.0D;
    /**
     * RAD * ERD_UMFANG
     */
    public static final double RAD_AEQUATOR_RADIUS = RAD * AEQUATOR_RADIUS;
    /**
     * RAD / 2.0D
     */
    public static final double RAD_HALBE = RAD / 2.0D;

    static private double getMittlererRadius()
    {
        return MITTLERER_RADIUS;
    }

    static private double getPolarRadius()
    {
        return POLAR_RADIUS;
    }

    static private double getRad()
    {
        return RAD;
    }

    private MercatorConstants()
    {
        super();
    }
}
