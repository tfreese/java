package de.freese.dependency.update.coordinate;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 * @since 28.05.23
 */
@FunctionalInterface
public interface CoordinateSupplier extends Supplier<List<Coordinate>> {

    static CoordinateSupplier from(final Set<Coordinate> coordinates) {
        return new CoordinateSupplierDefault(coordinates);
    }

    static CoordinateSupplier fromGradleProperties(final Path path) {
        return new CoordinateSupplierGradleProperties(path);
    }

    /**
     * Parse *.toml Files.
     **/
    static CoordinateSupplier fromGradleVersionCatalog(final Path path) {
        return new CoordinateSupplierGradleVersionCatalog(path);
    }

    static CoordinateSupplier fromIvy(final Path path) {
        return new CoordinateSupplierIvy(path);
    }

    static CoordinateSupplier fromMavenPom(final Path path) {
        return new CoordinateSupplierMavenPom(path);
    }

    static CoordinateSupplier fromMavenRepository(final Path path) {
        return new CoordinateSupplierMavenRepository(path);
    }
}
