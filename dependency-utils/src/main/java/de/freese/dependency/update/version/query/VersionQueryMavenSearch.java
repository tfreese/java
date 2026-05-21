// Created: 03 Apr. 2025
package de.freese.dependency.update.version.query;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

import de.freese.dependency.update.client.RepositoryClient;

/**
 * @author Thomas Freese
 */
final class VersionQueryMavenSearch extends AbstractVersionQuery {

    private static final String URI_MAVEN_SEARCH = "https://search.maven.org/solrsearch/select?q=g:{groupId}+AND+a:{artifactId}&core=gav&rows=10&wt=json";

    VersionQueryMavenSearch(final RepositoryClient repositoryClient) {
        super(repositoryClient);
    }

    @Override
    protected Set<String> loadVersions(final String groupId, final String artifactId, final Set<URI> repositories) {
        // try {
        //     uri = new URI(repository.getScheme(), repository.getUserInfo(), repository.getHost(), repository.getPort(), repository.getPath(), query, null);
        // }
        // catch (URISyntaxException ex) {
        //     throw new IllegalArgumentException(ex.getMessage(), ex);
        // }

        final URI uri = URI.create(URI_MAVEN_SEARCH.replace("{groupId}", groupId).replace("{artifactId}", artifactId));

        return new TreeSet<>(getRepositoryClient().getVersionsByMavenSearch(uri));
    }
}
