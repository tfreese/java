package de.freese.dependency.update.client;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractRepositoryClientDecorator implements RepositoryClient {
    private final RepositoryClient delegate;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractRepositoryClientDecorator(final RepositoryClient delegate) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean exist(final URI uri) {
        return delegate.exist(uri);
    }

    @Override
    public List<String> getVersionsByMavenSearch(final URI uri) {
        return delegate.getVersionsByMavenSearch(uri);
    }

    @Override
    public List<String> getVersionsByMetaData(final URI uri) {
        return delegate.getVersionsByMetaData(uri);
    }

    protected Logger getLogger() {
        return logger;
    }
}
