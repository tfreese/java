// Created: 03.05.2021
package de.freese.maven.proxy.repository.file;

import java.io.IOException;
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
public class FileRepository extends AbstractRepository {

    private final Repository delegate;

    private final Path fileCachePath;

    public FileRepository(final Repository delegate, final Path fileCachePath) throws IOException {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (!Files.exists(fileCachePath)) {
            throw new IOException("path not exist: " + fileCachePath);
        }

        if (!Files.isWritable(fileCachePath)) {
            throw new IOException("path not writeable: " + fileCachePath);
        }

        this.fileCachePath = Objects.requireNonNull(fileCachePath, "fileCachePath required");
    }

    @Override
    public boolean exist(final URI resource) throws Exception {
        final Path path = toPath(resource);

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

        final Path path = toPath(resource);

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

                return new FileRepositoryResponse(response, path);
            }
        }

        return null;
    }

    private Path toPath(final URI resource) {
        Path path = null;
        String key = resource.getPath();
        key = key.replace(' ', '_');

        if (key.startsWith("/")) {
            path = this.fileCachePath.resolve(key.substring(1));
        }
        else {
            path = this.fileCachePath.resolve(key);
        }

        return path;
    }
}
