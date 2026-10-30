package de.freese.dependency.update.version.query;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.dependency.update.client.RepositoryClient;

abstract class AbstractVersionQuery implements VersionQuery {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RepositoryClient repositoryClient;

    AbstractVersionQuery(final RepositoryClient repositoryClient) {
        super();

        this.repositoryClient = Objects.requireNonNull(repositoryClient, "repositoryClient required");
    }

    @Override
    public final Set<String> queryVersions(final String groupId, final String artifactId, final Set<URI> repositories) {
        getLogger().info("Query {}:{}", groupId, artifactId);

        return loadVersions(groupId, artifactId, repositories);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected RepositoryClient getRepositoryClient() {
        return repositoryClient;
    }

    protected abstract Set<String> loadVersions(String groupId, String artifactId, Set<URI> repositories);
}
