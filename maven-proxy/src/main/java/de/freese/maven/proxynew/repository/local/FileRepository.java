// Created: 22.07.23
package de.freese.maven.proxynew.repository.local;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.utils.HttpMethod;
import de.freese.maven.proxynew.repository.AbstractRepository;
import de.freese.maven.proxynew.repository.LocalRepository;

/**
 * @author Thomas Freese
 */
public class FileRepository extends AbstractRepository implements LocalRepository {

    private final Path path;

    public FileRepository(final String name, Path path) {
        super(name);

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public boolean exist(final URI resource) throws Exception {
        if (!isStarted()) {
            return false;
        }

        Path path = toPath(resource);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("lookup: {}", path);
        }

        return Files.exists(path);
    }

    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception {
        if (!isStarted()) {
            return null;
        }

        Path path = toPath(resource);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("lookup: {}", path);
        }

        if (Files.exists(path)) {
            return new RepositoryResponse(resource, Files.size(path), Files.newInputStream(path));
        }

        return null;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public boolean supports(final HttpMethod httpMethod) {
        return HttpMethod.HEAD.equals(httpMethod) || HttpMethod.GET.equals(httpMethod) || HttpMethod.PUT.equals(httpMethod);
    }

    @Override
    public String toString() {
        return getName() + ": " + getPath().toString();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (!Files.isReadable(path)) {
            throw new IllegalStateException("path not readable: " + path);
        }

        if (!Files.isWritable(path)) {
            throw new IllegalStateException("path not writeable: " + path);
        }
    }

    private Path toPath(final URI resource) {
        Path relativePath = toRelativePath(resource);

        return getPath().resolve(relativePath);
    }
}
