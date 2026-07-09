// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.dependency.utils.Utils;

/**
 * @author Thomas Freese
 */
final class CoordinateSupplierMavenRepository implements CoordinateSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoordinateSupplierMavenRepository.class);

    private static List<Path> getPomPaths(final Path localRepository) throws IOException {
        final BiPredicate<Path, BasicFileAttributes> matcher = (p, attributes) -> p.toString().endsWith(".pom");

        try (Stream<Path> stream = Files.find(localRepository, 10, matcher)) {
            return stream.toList();
        }
    }

    private final Path path;

    CoordinateSupplierMavenRepository(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public List<Coordinate> get() {
        final List<Coordinate> coordinates = new ArrayList<>();

        try {
            final List<Path> pomPaths = getPomPaths(path);

            for (final Path pomPath : pomPaths) {
                final Path versionPath = pomPath.getParent();
                final Path artifactIdPath = Objects.requireNonNull(versionPath).getParent();
                final Path groupIdPath = path.relativize(Objects.requireNonNull(artifactIdPath).getParent());

                final String version = versionPath.getFileName().toString();
                final String artifactId = artifactIdPath.getFileName().toString();
                final String groupId = groupIdPath.toString().replace('/', '.');

                coordinates.add(new Coordinate(groupId, artifactId, version, Utils.toSource(path)));
            }
        }
        catch (final IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return coordinates;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoordinateSupplierMavenRepository.class.getSimpleName() + "[", "]")
                .add("path=" + path)
                .toString();
    }
}
