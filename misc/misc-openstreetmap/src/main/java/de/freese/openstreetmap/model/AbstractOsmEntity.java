// Created: 06.11.2011
package de.freese.openstreetmap.model;

import java.util.HashMap;
import java.util.Map;

/**
 * BasisObject des OSM Modells.
 *
 * @author Thomas Freese
 */
public abstract class AbstractOsmEntity {
    private final Map<String, String> tags = new HashMap<>();

    private long id;

    public long getID() {
        return this.id;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    public void setID(final long id) {
        this.id = id;
    }
}
