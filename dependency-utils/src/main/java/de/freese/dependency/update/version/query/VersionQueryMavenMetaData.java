// Created: 03 Apr. 2025
package de.freese.dependency.update.version.query;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.freese.dependency.update.client.RepositoryClient;

/**
 * @author Thomas Freese
 */
final class VersionQueryMavenMetaData extends AbstractVersionQuery {
    private static URI createMetaDataUri(final URI repositoryUri, final String groupId, final String artifactId) {
        String path = repositoryUri.getPath();

        if (!path.endsWith("/")) {
            path += "/";
        }

        path += groupId.replace(".", "/") + "/" + artifactId + "/maven-metadata.xml";

        return repositoryUri.resolve(path);
    }

    VersionQueryMavenMetaData(final RepositoryClient repositoryClient) {
        super(repositoryClient);
    }

    @Override
    protected Set<String> loadVersions(final String groupId, final String artifactId, final Set<URI> repositories) {
        final Set<String> versions = new TreeSet<>();

        for (final URI repositoryUri : repositories) {
            final URI uri = createMetaDataUri(repositoryUri, groupId, artifactId);

            if (!getRepositoryClient().exist(uri)) {
                continue;
            }

            final List<String> versionsResult = getRepositoryClient().getVersionsByMetaData(uri);

            versions.addAll(versionsResult);
        }

        return versions;
    }
}
