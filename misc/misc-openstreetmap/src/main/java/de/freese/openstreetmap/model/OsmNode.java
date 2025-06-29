// Created: 06.11.2011
package de.freese.openstreetmap.model;

/**
 * Enthält die Koordinateninformationen eines geografischen Punktes.
 *
 * @author Thomas Freese
 */
public class OsmNode extends AbstractOsmEntity {
    /**
     * Längengrad.<br>
     * >−90.0 and <90.0
     */
    private float latitude;
    /**
     * Breitengrad.<br>
     * >−180 and <180
     */
    private float longitude;

    /**
     * Längengrad.<br>
     * >−90.0 and <90.0
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Breitengrad.<br>
     * >−180 and <180
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Längengrad.<br>
     * >−90.0 and <90.0
     */
    public void setLatitude(final float latitude) {
        this.latitude = latitude;
    }

    /**
     * Breitengrad.<br>
     * >−180 and <180
     */
    public void setLongitude(final float longitude) {
        this.longitude = longitude;
    }
}
