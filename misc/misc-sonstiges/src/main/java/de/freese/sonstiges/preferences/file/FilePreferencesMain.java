// Created: 13.11.22
package de.freese.sonstiges.preferences.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Thomas Freese
 */
public final class FilePreferencesMain
{
    public static void main(final String[] args) throws BackingStoreException, IOException
    {
        System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory.class.getName());
        System.setProperty(FilePreferencesFactory.SYSTEM_PROPERTY_FILE, "myprefs.txt");

        Preferences p = Preferences.userRoot();// Preferences.userNodeForPackage(List.class);

        for (String s : p.keys())
        {
            System.out.println("p[" + s + "]=" + p.get(s, null));
        }

        p.putBoolean("hi", true);
        p.put("Number", String.valueOf(System.currentTimeMillis()));

        p = p.node("test");
        System.out.println(p.get("user", null));
        p.put("user", "freese");
        System.out.println(new String(p.getByteArray("test", "null".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        p.putByteArray("test", "Thomas Freese".getBytes(StandardCharsets.UTF_8));

        Preferences.userRoot().exportSubtree(System.out);
    }

    private FilePreferencesMain()
    {
        super();
    }
}
