// Created: 28.05.23
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
 */
public final class PropertyResolver implements Supplier<Map<String, String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyResolver.class);

    private final List<PropertySupplier> propertySuppliers = new ArrayList<>();

    public PropertyResolver add(final PropertySupplier propertySupplier) {
        this.propertySuppliers.add(Objects.requireNonNull(propertySupplier, "propertySupplier required"));

        return this;
    }

    public PropertyResolver addMaven(final Path path) {
        add(PropertySupplier.ofMavenPom(Objects.requireNonNull(path, "path required")));

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
