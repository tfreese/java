// Created: 28.05.23
package de.freese.dependency.update.repository;

import java.net.URI;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface RepositorySupplier extends Supplier<Set<URI>> {
    static RepositorySupplier of(final Set<URI> repositories) {
        return new RepositorySupplierManual(repositories);
    }

    static RepositorySupplier of(final URI repository) {
        return new RepositorySupplierManual(Set.of(repository));
    }

    static RepositorySupplier of(final String uri) {
        return of(URI.create(uri));
    }

    static RepositorySupplier ofMavenSettings(final Path path) {
        return new RepositorySupplierMavenSettings(path);
    }

    /**
     * Default: {@code Paths.get(System.getProperty("user.home"), ".m2", "settings.xml")}
     */
    static RepositorySupplier ofMavenSettings() {
        return ofMavenSettings(Path.of(System.getProperty("user.home"), ".m2", "settings.xml"));
    }
}
