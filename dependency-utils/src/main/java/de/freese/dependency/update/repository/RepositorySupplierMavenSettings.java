// Created: 28.05.23
package de.freese.dependency.update.repository;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingResult;

/**
 * @author Thomas Freese
 */
final class RepositorySupplierMavenSettings implements RepositorySupplier {

    private static Set<URI> parseSettings(final Settings settings) {
        final Set<URI> repositories = new LinkedHashSet<>();

        // Mirrors
        settings.getMirrors().forEach(mirror -> {
            final String url = mirror.getUrl();
            repositories.add(URI.create(url));
        });

        // Active Profiles
        final List<Profile> activeProfiles = settings.getActiveProfiles().stream().map(ap -> settings.getProfilesAsMap().get(ap)).toList();
        final List<Repository> profileRepositories = activeProfiles.stream().flatMap(p -> p.getRepositories().stream()).toList();
        final List<Repository> profilePluginRepositories = activeProfiles.stream().flatMap(p -> p.getPluginRepositories().stream()).toList();

        Stream.concat(profileRepositories.stream(), profilePluginRepositories.stream()).forEach(r -> repositories.add(URI.create(r.getUrl())));

        return repositories;
    }

    private final Path path;

    RepositorySupplierMavenSettings(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public Set<URI> get() {
        final File mavenSettingsFile = path.toFile();

        DefaultSettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest()
                .setSystemProperties(System.getProperties())
                .setUserProperties(null)
                .setUserSettingsFile(mavenSettingsFile);

        final String mvnHome = Optional.ofNullable(System.getenv("M2_HOME")).orElse(System.getProperty("M2_HOME"));

        if (mvnHome != null) {
            final Path m2HomeSettings = Path.of(mvnHome, "conf", "settings.xml");

            if (Files.isReadable(m2HomeSettings)) {
                settingsBuildingRequest = settingsBuildingRequest.setGlobalSettingsFile(m2HomeSettings.toFile());
            }
        }

        final SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();

        final SettingsBuildingResult settingsBuildingResult;

        try {
            settingsBuildingResult = settingsBuilder.build(settingsBuildingRequest);
        }
        catch (final SettingsBuildingException ex) {
            throw new RuntimeException(ex);
        }

        final Settings settings = settingsBuildingResult.getEffectiveSettings();

        return parseSettings(settings);
    }
}
