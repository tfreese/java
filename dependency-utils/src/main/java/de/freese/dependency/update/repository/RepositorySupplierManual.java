// Created: 28.05.23
package de.freese.dependency.update.repository;

import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
final class RepositorySupplierManual implements RepositorySupplier {
    private final Set<URI> repositories = new TreeSet<>();

    RepositorySupplierManual(final Set<URI> repositories) {
        super();

        this.repositories.addAll(Objects.requireNonNull(repositories, "repositories required"));
    }

    @Override
    public Set<URI> get() {
        return Collections.unmodifiableSet(repositories);
    }
}
