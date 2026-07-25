package de.freese.dependency.update;

import java.util.function.Consumer;

import de.freese.dependency.update.coordinate.CoordinateResolver;
import de.freese.dependency.update.property.PropertyResolver;
import de.freese.dependency.update.repository.RepositoryResolver;

/**
 * @author Thomas Freese
 * @since 25.07.26
 */
public final class VersionUpdatesBuilder {
    private final CoordinateResolver coordinateResolver = new CoordinateResolver();
    private final PropertyResolver propertyResolver = new PropertyResolver();
    private final RepositoryResolver repositoryResolver = new RepositoryResolver();

    VersionUpdatesBuilder() {
        super();
    }

    public VersionUpdates build() {
        return new VersionUpdates(repositoryResolver, propertyResolver, coordinateResolver);
    }

    public VersionUpdatesBuilder configureCoordinateResolver(final Consumer<CoordinateResolver> consumer) {
        consumer.accept(coordinateResolver);

        return this;
    }

    public VersionUpdatesBuilder configurePropertyResolver(final Consumer<PropertyResolver> consumer) {
        consumer.accept(propertyResolver);

        return this;
    }

    public VersionUpdatesBuilder configureRepositoryResolver(final Consumer<RepositoryResolver> consumer) {
        consumer.accept(repositoryResolver);

        return this;
    }
}
