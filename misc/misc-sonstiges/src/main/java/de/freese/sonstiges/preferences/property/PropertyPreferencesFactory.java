// Created: 16.03.2009
package de.freese.sonstiges.preferences.property;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * PropertyImplementierung einer PreferencesFactory.
 *
 * @author Thomas Freese
 */
public final class PropertyPreferencesFactory implements PreferencesFactory
{
    private static Preferences SYSTEM_ROOT;

    private static Preferences USER_ROOT;

    /**
     * @see java.util.prefs.PreferencesFactory#systemRoot()
     */
    @Override
    public Preferences systemRoot()
    {
        if (SYSTEM_ROOT == null)
        {
            SYSTEM_ROOT = new PropertyPreferences();
        }

        return SYSTEM_ROOT;
    }

    /**
     * @see java.util.prefs.PreferencesFactory#userRoot()
     */
    @Override
    public Preferences userRoot()
    {
        if (USER_ROOT == null)
        {
            USER_ROOT = new PropertyPreferences();
        }

        return USER_ROOT;
    }
}
