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
import de.freese.maven.proxy.util.ProxyUtils;

/**
 * @author Thomas Freese
 */
public class FileRepository extends AbstractRepository {
    private final Repository delegate;

    private final Path fileCachePath;

    public FileRepository(final Path fileCachePath, final Repository delegate) throws IOException {
        super();

        if (!Files.exists(fileCachePath)) {
            throw new IOException("path not exist: " + fileCachePath);
        }

        if (!Files.isWritable(fileCachePath)) {
            throw new IOException("path not writeable: " + fileCachePath);
        }

        this.fileCachePath = Objects.requireNonNull(fileCachePath, "fileCachePath required");
        this.delegate = Objects.requireNonNull(delegate, "delegate required");
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#exist(java.net.URI)
     */
    @Override
    public boolean exist(final URI resource) throws Exception {
        final Path path = toPath(resource);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("lookup: {}", path);
        }

        // Erst auf der Platte suchen.
        if (Files.exists(path)) {
            return true;
        }

        // Dann erst im Repository suchen.
        return this.delegate.exist(resource);
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getInputStream(java.net.URI)
     */
    @Override
    public RepositoryResponse getInputStream(final URI resource) throws Exception {
        final Path path = toPath(resource);

        if (path.endsWith("maven-metadata.xml")) {
            // Diese Daten nie speichern !
            return this.delegate.getInputStream(resource);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("lookup: {}", path);
        }

        // Erst auf der Platte suchen.
        if (Files.exists(path)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("use cached: {}", path);
            }

            return new RepositoryResponse(resource, Files.size(path), Files.newInputStream(path));
        }
        else {
            Files.createDirectories(path.getParent());

            // Dann erst im Repository suchen.
            RepositoryResponse response = this.delegate.getInputStream(resource);

            if (response != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Download {}, {} Bytes = {}", response.getUri(), response.getContentLength(), ProxyUtils.toHumanReadable(response.getContentLength()));
                }

                // Den InputStream gleichzeitig in den File- und Response-OutputStream schreiben.
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
