package de.freese.dependency.update.property;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 * @since 28.05.23
 */
@FunctionalInterface
public interface PropertySupplier extends Supplier<Map<String, String>> {
    static PropertySupplier fromIvySettings(final Path path) {
        return new PropertySupplierIvySettings(Objects.requireNonNull(path, "path required"));
    }

    static PropertySupplier fromMavenPom(final Path path) {
        return new PropertySupplierMavenPom(Objects.requireNonNull(path, "path required"));
    }

    /**
     * Looking for spring-boot-dependencies POM in ~/.m2/repository and ~/.gradle/caches/modules/files.
     */
    static PropertySupplier fromSpringBootDependencies() {
        return new PropertySupplierSpringBootDependencies();
    }
}
