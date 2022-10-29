// Created: 27.10.2009
package de.freese.sonstiges.preferences.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PreferencesFactory implementation that stores the preferences in a user-defined file. To use it, set the system property
 * <tt>java.util.prefs.PreferencesFactory</tt> to <tt>net.infotrek.util.prefs.FilePreferencesFactory</tt>
 * <p/>
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the system property <tt>net.infotrek.util.prefs.FilePreferencesFactory.file</tt>
 *
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferencesFactory.java,v 1.1 2009-11-14 16:12:02 tommy Exp $
 */
public class FilePreferencesFactory implements PreferencesFactory
{
    /**
     *
     */
    public static final String SYSTEM_PROPERTY_FILE = "net.infotrek.util.prefs.FilePreferencesFactory.file";
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FilePreferencesFactory.class);
    /**
     *
     */
    private static File preferencesFile;

    /**
     * @return {@link File}
     */
    public static File getPreferencesFile()
    {
        if (preferencesFile == null)
        {
            String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);

            if ((prefsFile == null) || (prefsFile.length() == 0))
            {
                prefsFile = System.getProperty("user.home") + File.separator + ".fileprefs";
            }

            preferencesFile = new File(prefsFile).getAbsoluteFile();
            LOGGER.debug("Preferences file is {}", preferencesFile);
        }

        return preferencesFile;
    }

    /**
     * @param args String[]
     *
     * @throws BackingStoreException Falls was schiefgeht.
     * @throws IOException Falls was schiefgeht.
     */
    public static void main(final String[] args) throws BackingStoreException, IOException
    {
        System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory.class.getName());
        System.setProperty(SYSTEM_PROPERTY_FILE, "myprefs.txt");

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

    /**
     *
     */
    private Preferences rootPreferences;

    /**
     * @see java.util.prefs.PreferencesFactory#systemRoot()
     */
    @Override
    public Preferences systemRoot()
    {
        return userRoot();
    }

    /**
     * @see java.util.prefs.PreferencesFactory#userRoot()
     */
    @Override
    public Preferences userRoot()
    {
        if (this.rootPreferences == null)
        {
            LOGGER.debug("Instantiating root preferences");

            this.rootPreferences = new FilePreferences(null, "");
        }

        return this.rootPreferences;
    }
}
