package de.freese.dependency.update.property;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.dependency.utils.PropertySubstitution;

/**
 * @author Thomas Freese
 * @since 28.05.23
 */
public final class PropertyResolver implements Supplier<Map<String, String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyResolver.class);

    private final List<PropertySupplier> propertySuppliers = new ArrayList<>();

    public PropertyResolver add(final PropertySupplier propertySupplier) {
        this.propertySuppliers.add(Objects.requireNonNull(propertySupplier, "propertySupplier required"));

        return this;
    }

    public PropertyResolver fromIvySettings(final Path path) {
        add(PropertySupplier.fromIvySettings(Objects.requireNonNull(path, "path required")));

        return this;
    }

    public PropertyResolver fromMavenPom(final Path path) {
        add(PropertySupplier.fromMavenPom(Objects.requireNonNull(path, "path required")));

        return this;
    }

    /**
     * Looking for spring-boot-dependencies POM in ~/.m2/repository and ~/.gradle/caches/modules/files.
     */
    public PropertyResolver fromSpringBootDependencies() {
        add(PropertySupplier.fromSpringBootDependencies());

        return this;
    }

    @Override
    public Map<String, String> get() {
        final Map<String, String> properties = new TreeMap<>();

        for (final PropertySupplier propertySupplier : propertySuppliers) {
            properties.putAll(propertySupplier.get());
        }

        LOGGER.info("Properties resolved: {}", properties.size());
        LOGGER.info("Resolve Placeholder");

        PropertySubstitution.replacePlaceHolder(properties);

        return properties;
    }
}
