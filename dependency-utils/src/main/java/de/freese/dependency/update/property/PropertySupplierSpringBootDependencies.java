// Created: 28.05.23
package de.freese.dependency.update.property;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.dependency.utils.MavenModelCache;

/**
 * @author Thomas Freese
 */
final class PropertySupplierSpringBootDependencies implements PropertySupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertySupplierSpringBootDependencies.class);
    private static final Pattern PATTERN_SPRING_BOOT_DEPENDENCIES = Pattern.compile("^spring-boot-dependencies-.*\\.pom$");

    private static Map<ComparableVersion, Path> lookupGradle() throws IOException {
        final Path pathCaches = Path.of(System.getProperty("user.home"), ".gradle", "caches");

        if (!Files.exists(pathCaches)) {
            return Map.of();
        }

        // Find modules-*/files-* Folder.
        final List<Path> pathModuleFiles = new ArrayList<>();

        try (DirectoryStream<Path> directoryStreamCaches = Files.newDirectoryStream(pathCaches, "modules-*")) {
            for (final Path module : StreamSupport.stream(directoryStreamCaches.spliterator(), false).toList()) {
                try (DirectoryStream<Path> directoryStreamModules = Files.newDirectoryStream(module, "files-*")) {
                    pathModuleFiles.addAll(StreamSupport.stream(directoryStreamModules.spliterator(), false).toList());
                }
            }
        }

        final Map<ComparableVersion, Path> versionPathMap = new TreeMap<>();

        for (final Path pathModuleFile : pathModuleFiles) {
            final Path pathSpringBootDependencies = pathModuleFile.resolve("org.springframework.boot").resolve("spring-boot-dependencies");

            if (!Files.exists(pathSpringBootDependencies)) {
                continue;
            }

            try (Stream<Path> stream = Files.find(pathSpringBootDependencies, 10, (path, bfa) -> bfa.isRegularFile())) {
                stream.filter(path -> {
                            final Matcher matcher = PATTERN_SPRING_BOOT_DEPENDENCIES.matcher(path.getFileName().toString());

                            return matcher.matches();
                        })
                        .forEach(pathPom -> {
                            final String version = pathPom.getName(pathPom.getNameCount() - 3).toString();

                            versionPathMap.put(new ComparableVersion(version), pathPom);
                        });
            }
        }

        return versionPathMap;
    }

    private static Map<ComparableVersion, Path> lookupMaven() throws IOException {
        final Path path = Path.of(System.getProperty("user.home"), ".m2", "repository", "org", "springframework", "boot", "spring-boot-dependencies");

        if (!Files.exists(path)) {
            return Map.of();
        }

        final Map<ComparableVersion, Path> versionPathMap = new TreeMap<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            StreamSupport.stream(directoryStream.spliterator(), false)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(ComparableVersion::new)
                    .forEach(version -> versionPathMap.put(version,
                            path.resolve(version.toString()).resolve("spring-boot-dependencies-%s.pom".formatted(version.toString()))
                    ));
        }

        return versionPathMap;
    }

    private static void toMap(final Map<String, String> map, final Properties properties) {
        if (properties == null) {
            return;
        }

        for (final String name : properties.stringPropertyNames()) {
            map.put(name, properties.getProperty(name));
        }
    }

    @Override
    public Map<String, String> get() {
        final NavigableMap<ComparableVersion, Path> versionPathMap = new TreeMap<>();

        try {
            versionPathMap.putAll(lookupGradle());
            versionPathMap.putAll(lookupMaven());
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }

        Path path = null;

        if (!versionPathMap.isEmpty()) {
            // Get the highest Entry = highest Version.
            path = versionPathMap.lastEntry().getValue();
        }

        if (path == null) {
            return Map.of();
        }

        if (LOGGER.isInfoEnabled()) {
            // String filePath = path.toString().replace(System.getProperty("user.home") + "/", "");

            final String filePath = StreamSupport.stream(path.spliterator(), false)
                    .skip(2) // Skip home-Directory
                    .map(Path::toString)
                    .map(element -> {
                        if (element.length() > 35 || "spring-boot-dependencies".equals(element)) {
                            // Gradle Checksum.
                            return "...";
                        }

                        return element;
                    })
                    .collect(Collectors.joining("/"));

            LOGGER.info("detected: ~/{}", filePath);
        }

        final Model model = MavenModelCache.get(path);
        final Map<String, String> map = new TreeMap<>();

        toMap(map, model.getProperties());

        for (final Profile profile : model.getProfiles()) {
            toMap(map, profile.getProperties());
        }

        return map;
    }
}
