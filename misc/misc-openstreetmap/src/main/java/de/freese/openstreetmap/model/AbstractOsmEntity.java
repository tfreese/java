// Created: 06.11.2011
package de.freese.openstreetmap.model;

import java.util.HashMap;
import java.util.Map;

/**
 * BasisObject des OSM Modells.
 *
 * @author Thomas Freese
 */
public abstract class AbstractOsmEntity
{
    /**
     *
     */
    public final Map<String, String> tags = new HashMap<>();
    /**
     *
     */
    public long id;

    /**
     * @return long
     */
    public long getID()
    {
        return this.id;
    }

    /**
     * @return Map<String, String>
     */
    public Map<String, String> getTags()
    {
        return this.tags;
    }

    /**
     * @param id long
     */
    public void setID(final long id)
    {
        this.id = id;
    }
}
