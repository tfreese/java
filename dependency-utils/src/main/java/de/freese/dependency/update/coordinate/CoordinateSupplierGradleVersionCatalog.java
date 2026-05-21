// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import de.freese.dependency.utils.Utils;

/**
 * Parse *.toml Files.
 *
 * @author Thomas Freese
 */
final class CoordinateSupplierGradleVersionCatalog implements CoordinateSupplier {
    private final Path path;

    CoordinateSupplierGradleVersionCatalog(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public List<Coordinate> get() {
        final List<String> lines;

        try {
            lines = Files.readAllLines(path);
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }

        // Remove all from start until [versions].
        for (final Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            final String line = iterator.next();

            iterator.remove();

            if (line.startsWith("[versions]")) {
                break;
            }
        }

        final Map<String, String> versions = new HashMap<>();

        for (final String line : lines) {
            if (line.isBlank()) {
                break;
            }

            final String[] splits = line.split("=");
            final String key = splits[0].strip();
            final String value = splits[1].strip().replace("\"", "");

            versions.put(key, value);
        }

        // Remove all from start until [libraries].
        for (final Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            final String line = iterator.next();

            iterator.remove();

            if (line.startsWith("[libraries]")) {
                break;
            }
        }

        final List<Coordinate> coordinates = new ArrayList<>();

        for (final String line : lines) {
            if (line.isBlank()) {
                break;
            }

            String[] splits = line.split("=");
            final String coordinate = splits[2].split(",")[0].replace("\"", "").strip();
            final String versionKey = splits[3].replace("\"", "").replace("}", "").strip();

            splits = coordinate.split(":");
            final String groupId = splits[0];
            final String artifactId = splits[1];
            final String version = versions.get(versionKey);

            coordinates.add(new Coordinate(groupId, artifactId, version, Utils.toSource(path)));
        }

        // // Remove all from start until [plugins].
        // for (final Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
        //     final String line = iterator.next();
        //
        //     iterator.remove();
        //
        //     if (line.startsWith("[plugins]")) {
        //         break;
        //     }
        // }
        //
        // for (String line : lines) {
        //     if (line.isBlank()) {
        //         break;
        //     }
        //
        //     String[] splits = line.split("=");
        //     final String coordinate = splits[2].split(",")[0].replace("\"", "").strip();
        //     final String versionKey = splits[3].replace("\"", "").replace("}", "").strip();
        //
        //     splits = coordinate.split(":");
        //     final String groupId = splits[0];
        //     final String artifactId = splits[1];
        //     final String version = versions.get(versionKey);
        //
        //     coordinates.add(new Coordinate(groupId, artifactId, version, Utils.toSource(path)));
        // }

        return coordinates;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoordinateSupplierGradleVersionCatalog.class.getSimpleName() + "[", "]")
                .add("path=" + path)
                .toString();
    }
}
