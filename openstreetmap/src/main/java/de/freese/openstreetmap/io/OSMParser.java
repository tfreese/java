package de.freese.openstreetmap.io;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.freese.openstreetmap.model.OsmModel;

/**
 * Interface für einen OSM-Parser.
 *
 * @author Thomas Freese
 * @since 12.03.2015
 */
public interface OSMParser {
    /**
     * Einlesen der Kartendaten.<br>
     * Der Stream wird NICHT geschlossen!
     */
    OsmModel parse(InputStream inputStream) throws Exception;

    /**
     * Einlesen der Kartendaten.
     */
    default OsmModel parse(final String zipFileName, final String zipEntryName) throws Exception {
        final OsmModel model;

        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            final ZipEntry entry = zipFile.getEntry(zipEntryName);

            try (InputStream is = zipFile.getInputStream(entry)) {
                model = parse(is);
            }
        }

        return model;
    }
}
