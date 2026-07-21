// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class CoordinateResolver implements Supplier<List<Coordinate>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoordinateResolver.class);

    private final List<CoordinateSupplier> coordinateSuppliers = new ArrayList<>();

    private Predicate<Coordinate> coordinateFilter = coord -> true;

    public CoordinateResolver add(final CoordinateSupplier coordinateSupplier) {
        this.coordinateSuppliers.add(Objects.requireNonNull(coordinateSupplier, "coordinateSupplier required"));

        return this;
    }

    public CoordinateResolver addGradle(final Path path) {
        add(CoordinateSupplier.ofGradleProperties(Objects.requireNonNull(path, "path required")));

        return this;
    }

    public CoordinateResolver addIvy(final Path path) {
        add(CoordinateSupplier.ofIvy(Objects.requireNonNull(path, "path required")));

        return this;
    }

    public CoordinateResolver addMaven(final Path path) {
        add(CoordinateSupplier.ofMavenPom(Objects.requireNonNull(path, "path required")));

        return this;
    }

    @Override
    public List<Coordinate> get() {
        final List<Coordinate> coordinates = new ArrayList<>(256);

        for (final CoordinateSupplier coordinateSupplier : coordinateSuppliers) {
            coordinateSupplier.get().stream()
                    .filter(coordinateFilter)
                    .forEach(coordinates::add);
        }

        LOGGER.info("Coordinates resolved: {}", coordinates.size());

        return coordinates;
    }

    public void setFilter(final Predicate<Coordinate> coordinateFilter) {
        this.coordinateFilter = Objects.requireNonNull(coordinateFilter, "coordinateFilter required");
    }
}
