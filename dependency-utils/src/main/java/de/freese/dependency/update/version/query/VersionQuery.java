// Created: 03.03.2019
package de.freese.dependency.update.version.query;

import java.net.URI;
import java.util.Set;

import de.freese.dependency.update.client.RepositoryClient;
import de.freese.dependency.update.coordinate.Coordinate;

/**
 * Provides available Versions for a {@link Coordinate}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface VersionQuery {
    static VersionQuery ofMavenMetaData(final RepositoryClient repositoryClient) {
        return new VersionQueryMavenMetaData(repositoryClient);
    }

    static VersionQuery ofMavenSearch(final RepositoryClient repositoryClient) {
        return new VersionQueryMavenSearch(repositoryClient);
    }

    Set<String> getVersions(String groupId, String artifactId, Set<URI> repositories);
}
