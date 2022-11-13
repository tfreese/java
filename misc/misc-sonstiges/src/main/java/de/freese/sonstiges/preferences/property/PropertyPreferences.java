// Created: 16.03.2009
package de.freese.sonstiges.preferences.property;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * PropertyImplementierung für Preferences.
 *
 * @author Thomas Freese
 */
public final class PropertyPreferences extends AbstractPreferences
{
    private final Map<String, PropertyPreferences> children = new TreeMap<>();

    private final Map<String, String> properties = new TreeMap<>();

    PropertyPreferences()
    {
        super(null, "");
    }

    private PropertyPreferences(final AbstractPreferences parent, final String name)
    {
        super(parent, name);

        this.newNode = true;
    }

    /**
     * @see java.util.prefs.AbstractPreferences#put(java.lang.String, java.lang.String)
     */
    @Override
    public void put(final String key, final String value)
    {
        synchronized (this.lock)
        {
            try
            {
                super.put(key, value);
            }
            catch (IllegalArgumentException ex)
            {
                if (ex.getMessage().contains("too long"))
                {
                    // Keine Längenbegrenzung.
                    putSpi(key, value);
                }
                else
                {
                    throw ex;
                }
            }
        }
    }

    /**
     * @see java.util.prefs.AbstractPreferences#childSpi(java.lang.String)
     */
    @Override
    protected AbstractPreferences childSpi(final String name)
    {
        PropertyPreferences child = this.children.get(name);

        if ((child == null) || child.isRemoved())
        {
            child = new PropertyPreferences(this, name);
            this.children.put(name, child);
        }

        return child;
    }

    /**
     * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
     */
    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException
    {
        Set<String> keys = this.children.keySet();

        return keys.toArray(new String[keys.size()]);
    }

    /**
     * @see java.util.prefs.AbstractPreferences#flushSpi()
     */
    @Override
    protected void flushSpi() throws BackingStoreException
    {
        // Hier eventuell als Datei speicherbar machen
    }

    /**
     * @see java.util.prefs.AbstractPreferences#getSpi(java.lang.String)
     */
    @Override
    protected String getSpi(final String key)
    {
        synchronized (this.lock)
        {
            return this.properties.get(key);
        }
    }

    /**
     * @see java.util.prefs.AbstractPreferences#keysSpi()
     */
    @Override
    protected String[] keysSpi() throws BackingStoreException
    {
        synchronized (this.lock)
        {
            return this.properties.keySet().toArray(new String[0]);
        }
    }

    /**
     * @see java.util.prefs.AbstractPreferences#putSpi(java.lang.String, java.lang.String)
     */
    @Override
    protected void putSpi(final String key, final String value)
    {
        synchronized (this.lock)
        {
            this.properties.put(key, value);
        }

        // flush();
    }

    /**
     * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
     */
    @Override
    protected void removeNodeSpi() throws BackingStoreException
    {
        // Empty
    }

    /**
     * @see java.util.prefs.AbstractPreferences#removeSpi(java.lang.String)
     */
    @Override
    protected void removeSpi(final String key)
    {
        synchronized (this.lock)
        {
            this.properties.remove(key);
        }

        // flush();
    }

    /**
     * @see java.util.prefs.AbstractPreferences#syncSpi()
     */
    @Override
    protected void syncSpi() throws BackingStoreException
    {
        if (isRemoved())
        {
        }

        // TODO Hier eventuell als Datei speicherbar machen
        // synchronized (this.lock)
        // {
        // // this.properties.clear();
        // }
    }
}
