package de.freese.dependency.update;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import reactor.core.scheduler.Schedulers;

import de.freese.dependency.update.client.RepositoryClient;
import de.freese.dependency.update.client.RepositoryClientType;
import de.freese.dependency.update.coordinate.Coordinate;
import de.freese.dependency.update.coordinate.CoordinateSupplier;
import de.freese.dependency.update.property.PropertySupplier;
import de.freese.dependency.update.repository.RepositorySupplier;
import de.freese.dependency.update.version.filter.VersionFilter;
import de.freese.dependency.update.version.query.VersionQuery;

/**
 * @author Thomas Freese
 * @since 28.05.23
 */
@SuppressWarnings({"java:S106", "java:S125", "java:S1192"})
public final class VersionUpdatesLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionUpdatesLauncher.class);

    static void main() {
        // Redirect Java-Util-Logger to Slf4J.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final Path basePath = Path.of(System.getProperty("user.home"), "git");
        final Path pathParents = basePath.resolve("parents");
        final Path pathParentsGradle = pathParents.resolve("gradle-projects");
        final Path pathParentsMaven = pathParents.resolve("maven-projects");
        final Path pathParentsIvy = pathParents.resolve("ivy-projects");

        final List<Path> mavenPoms = List.of(
                pathParentsMaven.resolve("maven-bom", "pom.xml"),
                pathParentsMaven.resolve("maven-parent", "pom.xml"),
                basePath.resolve("test-repo-release", "pom.xml")
        );

        final VersionUpdates versionUpdates = new VersionUpdates();

        // Repositories
        versionUpdates.configureRepositoryResolver(repositoryResolver -> {
            Predicate<URI> repositoryFilter = uri -> !"file".equalsIgnoreCase(uri.getScheme());
            repositoryFilter = repositoryFilter
                    .or(uri -> !"http://central".startsWith(uri.toString()))
                    .or(uri -> !"https://central".startsWith(uri.toString()));

            repositoryResolver
                    .setFilter(repositoryFilter)
                    .add(RepositorySupplier.ofMavenSettings())
                    // .add(RepositorySupplier.of("https://repo1.maven.org/maven2"))
                    // .add(RepositorySupplier.of("https://repository.primefaces.org"))
                    .add(RepositorySupplier.of("https://repo.gradle.org/gradle/libs-releases"))
                    .add(RepositorySupplier.of("https://plugins.gradle.org/m2"))
            ;
        });

        // Properties
        versionUpdates.configurePropertyResolver(propertyResolver -> {
                    propertyResolver
                            .add(PropertySupplier.ofSpringBootDependencies())
                            .add(PropertySupplier.ofIvySettings(pathParentsIvy.resolve("ivysettings.xml")))
                    ;

                    mavenPoms.forEach(propertyResolver::addMaven);
                }
        );

        // Coordinates
        versionUpdates.configureCoordinateResolver(coordinateResolver -> {
                    coordinateResolver
                            .add(CoordinateSupplier.ofGradleProperties())
                            .addGradle(basePath.resolve("syro", "gradle.properties"))
                            .addGradle(basePath.resolve("java", "misc", "misc-log4j3", "gradle.properties"))
                            .addGradle(pathParents.resolve("tools", "gradle.properties"))
                            .addGradle(pathParentsGradle.resolve("gradle-plugins-test", "gradle.properties"))
                            .addGradle(pathParentsGradle.resolve("gradle-test-1", "gradle.properties"))
                            .addGradle(pathParentsGradle.resolve("platformbom-example", "gradle.properties"))
                            .addIvy(pathParentsIvy.resolve("multi-module", "project-api", "ivy.xml"))
                            .addIvy(pathParentsIvy.resolve("multi-module", "project-impl", "ivy.xml"))
                            .setFilter(coordinate -> !coordinate.getGroupId().startsWith("de.freese"));

                    mavenPoms.forEach(coordinateResolver::addMaven);
                }
        );

        int errorCode = 0;

        try (RepositoryClient repositoryClient = RepositoryClientType.JRE_HTTPCLIENT.create(3, Duration.ofSeconds(3L))) {
            final VersionQuery versionQuery = VersionQuery.ofMavenMetaData(repositoryClient);
            // final VersionQuery versionQuery = VersionQuery.ofMavenSearch(repositoryClient);
            final VersionFilter versionFilter = VersionFilter.ofMavenRuleSet(pathParentsMaven.resolve("rule-set.xml"));
            // final VersionFilter versionFilter = VersionFilter.ofDefaultRegEx();

            final Instant start = Instant.now();
            final List<Coordinate> updates = versionUpdates.getUpdates(versionQuery, versionFilter);

            System.out.printf("%nQuery time: %,d ms%n", Duration.between(start, Instant.now()).toMillis());

            VersionUpdates.printUpdates(updates, System.out);
        }
        catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            errorCode = -1;
        }
        finally {
            Schedulers.shutdownNow();
        }

        System.exit(errorCode);
    }

    private VersionUpdatesLauncher() {
        super();
    }
}
