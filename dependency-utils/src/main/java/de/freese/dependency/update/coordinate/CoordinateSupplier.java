// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface CoordinateSupplier extends Supplier<List<Coordinate>> {
    /**
     * Default: {@code Paths.get(System.getProperty("user.home"), ".gradle", "gradle.properties")}
     **/
    static CoordinateSupplier ofGradleProperties() {
        return ofGradleProperties(Path.of(System.getProperty("user.home"), ".gradle", "gradle.properties"));
    }

    static CoordinateSupplier ofGradleProperties(final Path path) {
        return new CoordinateSupplierGradleProperties(path);
    }

    /**
     * Parse *.toml Files.
     **/
    static CoordinateSupplier ofGradleVersionCatalog(final Path path) {
        return new CoordinateSupplierGradleVersionCatalog(path);
    }

    static CoordinateSupplier ofIvy(final Path path) {
        return new CoordinateSupplierIvy(path);
    }

    static CoordinateSupplier ofManual(final Set<Coordinate> coordinates) {
        return new CoordinateSupplierManual(coordinates);
    }

    static CoordinateSupplier ofMavenPom(final Path path) {
        return new CoordinateSupplierMavenPom(path);
    }

    static CoordinateSupplier ofMavenRepository(final Path path) {
        return new CoordinateSupplierMavenRepository(path);
    }
}
