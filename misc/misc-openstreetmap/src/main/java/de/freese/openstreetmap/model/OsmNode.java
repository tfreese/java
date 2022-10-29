// Created: 06.11.2011
package de.freese.openstreetmap.model;

/**
 * Enthält die Koordinateninformationen eines geografischen Punktes.
 *
 * @author Thomas Freese
 */
public class OsmNode extends AbstractOsmEntity
{
    /**
     * Längengrad.<br>
     * >−90.0 and <90.0
     */
    public float latitude;
    /**
     * Breitengrad.<br>
     * >−180 and <180
     */
    public float longitude;

    /**
     * Längengrad.<br>
     * >−90.0 and <90.0
     *
     * @return float
     */
    public float getLatitude()
    {
        return this.latitude;
    }

    /**
     * Breitengrad.<br>
     * >−180 and <180
     *
     * @return float
     */
    public float getLongitude()
    {
        return this.longitude;
    }

    /**
     * Längengrad.<br>
     * >−90.0 and <90.0
     *
     * @param latitude float
     */
    public void setLatitude(final float latitude)
    {
        this.latitude = latitude;
    }

    /**
     * Breitengrad.<br>
     * >−180 and <180
     *
     * @param longitude float
     */
    public void setLongitude(final float longitude)
    {
        this.longitude = longitude;
    }
}
