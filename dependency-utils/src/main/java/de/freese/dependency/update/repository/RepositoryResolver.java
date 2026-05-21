// Created: 28.05.23
package de.freese.dependency.update.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class RepositoryResolver implements Supplier<Set<URI>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryResolver.class);

    private final List<RepositorySupplier> repositorySuppliers = new ArrayList<>();

    private Predicate<URI> repositoryFilter = repo -> true;

    public RepositoryResolver add(final RepositorySupplier repositorySupplier) {
        this.repositorySuppliers.add(Objects.requireNonNull(repositorySupplier, "repositorySupplier required"));

        return this;
    }

    @Override
    public Set<URI> get() {
        final Set<URI> repositories = new LinkedHashSet<>();

        for (final Supplier<Set<URI>> repositorySupplier : repositorySuppliers) {
            repositorySupplier.get().stream()
                    .filter(repositoryFilter)
                    .forEach(repositories::add)
            ;
        }

        if (LOGGER.isInfoEnabled()) {
            final Function<URI, String> uriMapper = uri -> {
                if (uri.getPort() > 0) {
                    return uri.getHost() + ":" + uri.getPort() + uri.getPath();
                }

                return uri.getHost() + uri.getPath();
            };

            LOGGER.info("Repositories resolved: {}", repositories.stream().map(uriMapper).collect(Collectors.joining(", ")));
        }

        return repositories;
    }

    public void setRepositoryFilter(final Predicate<URI> repositoryFilter) {
        this.repositoryFilter = Objects.requireNonNull(repositoryFilter, "repositoryFilter required");
    }
}
