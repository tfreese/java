// Created: 28.05.23
package de.freese.dependency.update.property;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public Map<String, String> get() {
        final Map<String, String> properties = new TreeMap<>();

        for (final PropertySupplier propertySupplier : propertySuppliers) {
            properties.putAll(propertySupplier.get());
        }

        LOGGER.info("Properties resolved: {}", properties.size());
        LOGGER.info("Resolve Placeholder");

        for (final Map.Entry<String, String> entry : properties.entrySet()) {
            final String value = entry.getValue();

            if (value == null) {
                continue;
            }

            if (value.contains("${user.home}")) {
                entry.setValue(value.replace("${user.home}", System.getProperty("user.home")));
            } else if (value.contains("${java.version}")) {
                final String version = Optional.ofNullable(properties.get("java.version")).orElse(Integer.toString(Runtime.version().feature()));

                entry.setValue(value.replace("${java.version}", version));
            } else if (value.contains("${maven.build.timestamp}")) {
                final String format = Optional.ofNullable(properties.get("maven.build.timestamp.format")).orElse("yyyyMMdd-HHmmss");

                entry.setValue(value.replace("${maven.build.timestamp}", LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))));
            } else if (value.startsWith("${")) {
                String newValue = value;

                while (newValue.startsWith("${")) {
                    // Property in Property.
                    final String key = value.substring(2, value.length() - 1);
                    newValue = properties.get(key);
                }

                entry.setValue(newValue);
            }
        }

        return properties;
    }
}
