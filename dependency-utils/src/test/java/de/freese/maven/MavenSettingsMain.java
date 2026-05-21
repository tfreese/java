// Created: 10.03.23
package de.freese.maven;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.settings.crypto.DefaultSettingsDecrypter;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;

/**
 * @author Thomas Freese
 */
public final class MavenSettingsMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenSettingsMain.class);

    static void main() throws SettingsBuildingException {
        final Path path = Path.of(System.getProperty("user.home"), ".m2", "settings.xml");

        DefaultSettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest()
                .setSystemProperties(System.getProperties())
                .setUserProperties(null)
                .setUserSettingsFile(path.toFile());

        final String mvnHome = Optional.ofNullable(System.getenv("M2_HOME")).orElse(System.getProperty("M2_HOME"));

        if (mvnHome != null) {
            final Path m2HomeSettings = Path.of(mvnHome, "conf", "settings.xml");

            if (Files.isReadable(m2HomeSettings)) {
                settingsBuildingRequest = settingsBuildingRequest.setGlobalSettingsFile(m2HomeSettings.toFile());
            }
        }

        final SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();

        final SettingsBuildingResult settingsBuildingResult = settingsBuilder.build(settingsBuildingRequest);
        final Settings settings = settingsBuildingResult.getEffectiveSettings();

        // Passwörter entschlüsseln, ausserhalb vom Plexus Container die Objekte über Reflection zusammenbasteln.
        final DefaultSecDispatcher securityDispatcher = new DefaultSecDispatcher(new DefaultPlexusCipher());
        securityDispatcher.setConfigurationFile(System.getProperty("user.home") + "/.m2/settings-security.xml");

        final SettingsDecrypter settingsDecrypter = new DefaultSettingsDecrypter(securityDispatcher);

        final SettingsDecryptionRequest decryptionRequest = new DefaultSettingsDecryptionRequest(settings);
        final SettingsDecryptionResult decryptionResult = settingsDecrypter.decrypt(decryptionRequest);

        for (Server server : decryptionResult.getServers()) {
            LOGGER.info("Server = {} {}/{}", server.getId(), server.getUsername(), server.getPassword());
        }
    }

    private MavenSettingsMain() {
        super();
    }
}
