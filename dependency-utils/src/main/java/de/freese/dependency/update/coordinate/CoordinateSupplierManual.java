// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
final class CoordinateSupplierManual implements CoordinateSupplier {
    private final Set<Coordinate> coordinates = new TreeSet<>();

    CoordinateSupplierManual(final Set<Coordinate> coordinates) {
        super();

        this.coordinates.addAll(Objects.requireNonNull(coordinates, "coordinates required"));
    }

    @Override
    public List<Coordinate> get() {
        return List.copyOf(coordinates);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoordinateSupplierManual.class.getSimpleName() + "[", "]")
                .add("coordinates=" + coordinates)
                .toString();
    }
}
