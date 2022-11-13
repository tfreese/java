// Created: 07.04.2012
package de.freese.sonstiges.demos;

import java.io.File;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.settings.crypto.DefaultSettingsDecrypter;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

/**
 * @author Thomas Freese
 */
public final class MavenPomParserMain
{
    public static void main(final String[] args) throws Exception
    {
        mavenModel();
        mavenSettings();
    }

    private static void mavenModel()
    {
        File pomFile = new File("pom.xml");

        DefaultModelBuilderFactory modelBuilderFactory = new DefaultModelBuilderFactory();
        ModelBuilder modelBuilder = modelBuilderFactory.newInstance();
        // Result<Model> result = null;
        Model model = modelBuilder.buildRawModel(pomFile, 0, false).get();

        model.getDependencies().forEach(System.out::println);
        model.getProperties().forEach((key, value) -> System.out.printf("[%s - %s]%n", key, value));
        model.getRepositories().forEach(repo -> System.out.println(repo.getUrl()));
    }

    private static void mavenSettings() throws Exception
    {
        // @formatter:off
        DefaultSettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest()
                .setSystemProperties(System.getProperties())
                .setUserProperties(null)
                .setGlobalSettingsFile(new File(System.getenv("M2_HOME") + "/conf/settings.xml"))
                .setUserSettingsFile(new File(System.getProperty("user.home") + "/.m2/settings.xml"))
                ;
        // @formatter:on

        DefaultSettingsBuilderFactory settingsBuilderFactory = new DefaultSettingsBuilderFactory();
        SettingsBuilder settingsBuilder = settingsBuilderFactory.newInstance();
        SettingsBuildingResult settingsBuildingResult = settingsBuilder.build(settingsBuildingRequest);

        Settings settings = settingsBuildingResult.getEffectiveSettings();

        for (Server server : settings.getServers())
        {
            System.out.println("Server = " + server.getId() + " " + server.getUsername() + "/" + server.getPassword());
        }

        settings.getMirrors().forEach(System.out::println);

        Map<String, Profile> map = settings.getProfilesAsMap();
        settings.getActiveProfiles().stream().map(map::get).flatMap(p -> p.getRepositories().stream()).forEach(p -> System.out.println(p.getUrl()));

        System.out.println();

        // Passwörter entschlüsseln
        // Ausserhalb vom Plexus Container die Objekte über Reflection zusammenbasteln.
        SecDispatcher securityDispatcher = new DefaultSecDispatcher(new DefaultPlexusCipher());
        ((DefaultSecDispatcher) securityDispatcher).setConfigurationFile(System.getProperty("user.home") + "/.m2/settings-security.xml");

        SettingsDecrypter settingsDecrypter = new DefaultSettingsDecrypter(securityDispatcher);

        SettingsDecryptionRequest decryptionRequest = new DefaultSettingsDecryptionRequest(settings);
        SettingsDecryptionResult decryptionResult = settingsDecrypter.decrypt(decryptionRequest);

        for (Server server : decryptionResult.getServers())
        {
            System.out.println("Server = " + server.getId() + " " + server.getUsername() + "/" + server.getPassword());
        }
    }

    private MavenPomParserMain()
    {
        super();
    }
}
