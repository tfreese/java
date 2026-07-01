// Created: 28.05.23
package de.freese.dependency.update.property;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

import de.freese.dependency.utils.MavenModelCache;

/**
 * @author Thomas Freese
 */
final class PropertySupplierMavenPom implements PropertySupplier {
    private static void toMap(final Map<String, String> map, final Properties properties) {
        if (properties == null) {
            return;
        }

        for (final String name : properties.stringPropertyNames()) {
            map.put(name, properties.getProperty(name));
        }

        // properties.forEach((key, value) -> map.put((String) key, (String) value));

        // final Enumeration<String> enums = (Enumeration<String>) properties.propertyNames();
        //
        // while (enums.hasMoreElements()) {
        // final String key = enums.nextElement();
        // final String value = properties.getProperty(key);
        //
        // map.put(key, value);
        // }
    }

    private final Path path;

    PropertySupplierMavenPom(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public Map<String, String> get() {
        final Model model = MavenModelCache.get(path);
        final Map<String, String> map = new TreeMap<>();

        toMap(map, model.getProperties());

        for (final Profile profile : model.getProfiles()) {
            toMap(map, profile.getProperties());
        }

        map.entrySet().forEach(entry -> {
            final String value = entry.getValue();

            if (value == null) {
                return;
            }

            if (value.contains("${project.groupId}")) {
                entry.setValue(value.replace("${project.groupId}", model.getGroupId()));
            } else if (value.contains("${project.artifactId}")) {
                entry.setValue(value.replace("${project.artifactId}", model.getArtifactId()));
            } else if (value.contains("${project.version}")) {
                entry.setValue(value.replace("${project.version}", model.getVersion()));
            } else if (value.contains("${project.basedir}")) {
                entry.setValue(value.replace("${project.basedir}", path.getParent().toString()));
            } else if (value.contains("${project.build.directory}")) {
                entry.setValue(value.replace("${project.build.directory}", path.getParent().resolve("target").toString()));
            } else if (value.contains("${maven.build.timestamp}")) {
                final String format = Optional.ofNullable(map.get("maven.build.timestamp.format")).orElse("yyyyMMdd-HHmmss");

                entry.setValue(value.replace("${maven.build.timestamp}", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(format))));
            }
        });

        return map;
    }
}
