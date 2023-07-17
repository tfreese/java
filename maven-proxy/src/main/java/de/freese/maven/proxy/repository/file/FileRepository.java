// Created: 03.05.2021
package de.freese.maven.proxy.repository.file;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import de.freese.maven.proxy.repository.AbstractRepository;
import de.freese.maven.proxy.repository.RepositoryResponse;

/**
 * @author Thomas Freese
 */
public class FileRepository extends AbstractRepository {

    private final Path repositoryPath;

    public FileRepository(final Path repositoryPath) {
        super();

        this.repositoryPath = Objects.requireNonNull(repositoryPath, "repositoryPath required");

        if (!Files.exists(repositoryPath)) {
            throw new IllegalStateException("path not exist: " + repositoryPath);
        }

        if (!Files.isReadable(repositoryPath)) {
            throw new IllegalStateException("path not readable: " + repositoryPath);
        }
    }

    @Override
    public boolean exist(final URI resource) throws Exception {
        Path path = toPath(resource);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("lookup: {}", path);
        }

        return Files.exists(path);
    }

    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception {
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
    public String toString() {
        return this.repositoryPath.toString();
    }

    private Path toPath(final URI resource) {
        Path relativePath = toRelativePath(resource);

        return this.repositoryPath.resolve(relativePath);
    }
}
