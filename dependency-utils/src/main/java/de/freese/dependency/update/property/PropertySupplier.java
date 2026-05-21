// Created: 28.05.23
package de.freese.dependency.update.property;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface PropertySupplier extends Supplier<Map<String, String>> {
    static PropertySupplier ofIvySettings(final Path path) {
        return new PropertySupplierIvySettings(path);
    }

    static PropertySupplier ofMavenPom(final Path path) {
        return new PropertySupplierMavenPom(path);
    }

    static PropertySupplier ofSpringBootDependencies() {
        return new PropertySupplierSpringBootDependencies();
    }
}
