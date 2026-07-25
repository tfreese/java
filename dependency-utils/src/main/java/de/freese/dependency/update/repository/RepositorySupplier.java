package de.freese.dependency.update.repository;

import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 * @since 28.05.23
 */
@FunctionalInterface
public interface RepositorySupplier extends Supplier<Set<URI>> {
    static RepositorySupplier from(final URI repository) {
        return new RepositorySupplierDefault(Set.of(Objects.requireNonNull(repository, "repository required")));
    }

    static RepositorySupplier fromMavenSettings(final Path path) {
        return new RepositorySupplierMavenSettings(Objects.requireNonNull(path, "path required"));
    }
}
