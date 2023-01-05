// Created: 12.03.2015
package de.freese.openstreetmap.io;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.freese.openstreetmap.model.OsmModel;

/**
 * Interface für einen OSM-Parser.
 *
 * @author Thomas Freese
 */
public interface OSMParser
{
    /**
     * Einlesen der Kartendaten.<br>
     * Der Stream wird NICHT geschlossen !
     */
    OsmModel parse(InputStream inputStream) throws Exception;

    /**
     * Einlesen der Kartendaten.
     */
    default OsmModel parse(final String zipFileName, final String zipEntryName) throws Exception
    {
        OsmModel model = null;

        try (ZipFile zipFile = new ZipFile(zipFileName))
        {
            ZipEntry entry = zipFile.getEntry(zipEntryName);

            try (InputStream is = zipFile.getInputStream(entry))
            {
                model = parse(is);
            }
        }

        return model;
    }
}
