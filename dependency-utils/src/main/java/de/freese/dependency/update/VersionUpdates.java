// Created: 28.05.23
package de.freese.dependency.update;

import java.awt.Toolkit;
import java.io.PrintStream;
import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.dependency.update.coordinate.Coordinate;
import de.freese.dependency.update.coordinate.CoordinateResolver;
import de.freese.dependency.update.property.PropertyResolver;
import de.freese.dependency.update.repository.RepositoryResolver;
import de.freese.dependency.update.version.VersionResolver;
import de.freese.dependency.update.version.filter.VersionFilter;
import de.freese.dependency.update.version.query.VersionQuery;

/**
 * @author Thomas Freese
 */
public final class VersionUpdates {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionUpdates.class);

    public static void printUpdates(final Collection<Coordinate> coordinates, final PrintStream printStream) {
        printStream.println();

        if (coordinates.isEmpty()) {
            printStream.println("No updates available.");
            printStream.println();

            return;
        }

        Toolkit.getDefaultToolkit().beep();

        printStream.println("There are new updates:");

        // reduce(0, Integer::max);
        final int maxLengthCoordinate = coordinates.stream()
                .map(Coordinate::getGroupIdAndArtifactId)
                .mapToInt(String::length)
                .max()
                .orElse(0)
                + 5;

        final int maxLengthVersion = coordinates.stream()
                .map(coordinate -> coordinate.getVersionCurrent() + " -> " + coordinate.getVersionNewest())
                .mapToInt(String::length)
                .max()
                .orElse(0)
                + 5;

        coordinates.stream()
                .map(coordinate -> {
                    final String coordinateString = coordinate.getGroupIdAndArtifactId();
                    final String versionString = coordinate.getVersionCurrent() + " -> " + coordinate.getVersionNewest();

                    return coordinateString
                            + " "
                            + ".".repeat(maxLengthCoordinate - coordinateString.length())
                            + " "
                            + versionString
                            + " ".repeat(maxLengthVersion - versionString.length())
                            + coordinate.getSource();
                })
                .distinct()
                .sorted()
                .forEach(printStream::println)
        ;

        printStream.println();
        printStream.flush();
    }

    private final CoordinateResolver coordinateResolver = new CoordinateResolver();
    private final PropertyResolver propertyResolver = new PropertyResolver();
    private final RepositoryResolver repositoryResolver = new RepositoryResolver();

    public VersionUpdates() {
        super();
    }

    public void configureCoordinateResolver(final Consumer<CoordinateResolver> consumer) {
        consumer.accept(coordinateResolver);
    }

    public void configurePropertyResolver(final Consumer<PropertyResolver> consumer) {
        consumer.accept(propertyResolver);
    }

    public void configureRepositoryResolver(final Consumer<RepositoryResolver> consumer) {
        consumer.accept(repositoryResolver);
    }

    public List<Coordinate> getUpdates(final VersionQuery versionQuery, final VersionFilter versionFilter) {
        final Set<URI> repositories = repositoryResolver.get();
        final Map<String, String> properties = propertyResolver.get();

        final List<Coordinate> coordinates = coordinateResolver.get().stream()
                .filter(coordinate -> coordinate.getVersionCurrent() != null) // version = null -> Property is defined in BOM/Parent-POM -> ignore.
                .map(coordinate -> {
                    String version = coordinate.getVersionCurrent();

                    if (version.startsWith("${")) {
                        final String key = version.substring(2, version.length() - 1);
                        version = properties.get(key);

                        coordinate.setVersionCurrent(version);
                    }

                    return coordinate;
                })
                .sorted(Comparator.naturalOrder())
                .filter(coordinate -> {
                    if (coordinate.getVersionCurrent().startsWith("${")) {
                        LOGGER.warn("Found unresolved Placeholder: {}", coordinate);

                        return false;
                    }

                    return true;
                })
                .toList();

        final VersionResolver versionResolver = new VersionResolver(versionQuery, versionFilter);

        return QueryExecutor.getUpdates(versionResolver, coordinates, repositories);
    }
}
