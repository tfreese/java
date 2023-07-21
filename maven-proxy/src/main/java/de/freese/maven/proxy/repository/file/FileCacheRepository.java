// Created: 03.05.2021
package de.freese.maven.proxy.repository.file;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import de.freese.maven.proxy.repository.AbstractRepository;
import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.utils.ProxyUtils;

/**
 * @author Thomas Freese
 */
public class FileCacheRepository extends AbstractRepository {

    private final Repository delegate;

    private final Path fileCachePath;

    public FileCacheRepository(final Repository delegate, final Path fileCachePath) throws Exception {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (!Files.isWritable(fileCachePath)) {
            throw new IllegalStateException("path not writeable: " + fileCachePath);
        }

        if (!Files.exists(fileCachePath)) {
            Files.createDirectories(fileCachePath);
        }

        this.fileCachePath = Objects.requireNonNull(fileCachePath, "fileCachePath required");
    }

    @Override
    public boolean exist(final URI resource) throws Exception {
        Path path = toPath(resource);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("lookup: {}", path);
        }

        if (Files.exists(path)) {
            return true;
        }

        return this.delegate.exist(resource);
    }

    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception {
        if (resource.getPath().endsWith("maven-metadata.xml")) {
            // Never save these files, versions:display-dependency-updates won't work !
            return this.delegate.getInputStream(resource);
        }

        Path path = toPath(resource);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("lookup: {}", path);
        }

        if (Files.exists(path)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("use cached: {}", path);
            }

            return new RepositoryResponse(resource, Files.size(path), Files.newInputStream(path));
        }
        else {
            Files.createDirectories(path.getParent());

            RepositoryResponse response = this.delegate.getInputStream(resource);

            if (response != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Download {}, {} Bytes = {}", response.getUri(), response.getContentLength(), ProxyUtils.toHumanReadable(response.getContentLength()));
                }

                return new FileCacheRepositoryResponse(response, path);
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.fileCachePath.toString();
    }

    private Path toPath(final URI resource) {
        Path relativePath = toRelativePath(resource);

        return this.fileCachePath.resolve(relativePath);
    }
}
