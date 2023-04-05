// Created: 03.05.2021
package de.freese.maven.proxy.repository.file;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import de.freese.maven.proxy.repository.RepositoryResponse;

/**
 * @author Thomas Freese
 */
public class FileRepositoryResponse extends RepositoryResponse {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private final Path path;

    public FileRepositoryResponse(final RepositoryResponse repositoryResponse, Path path) {
        super(repositoryResponse.getUri(), repositoryResponse.getContentLength(), repositoryResponse.getInputStream());

        this.path = Objects.requireNonNull(path, "path required");
    }

    /**
     * Den InputStream gleichzeitig in den File- und Response-OutputStream schreiben.
     *
     * @see de.freese.maven.proxy.repository.RepositoryResponse#transferTo(java.io.OutputStream)
     */
    @Override
    public long transferTo(final OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        long transferred = 0L;

        try (InputStream inputStream = getInputStream(); OutputStream fileOutputStream = new BufferedOutputStream(Files.newOutputStream(path), DEFAULT_BUFFER_SIZE)) {
            while ((read = inputStream.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
                fileOutputStream.write(buffer, 0, read);
                outputStream.write(buffer, 0, read);

                transferred += read;
            }

            fileOutputStream.flush();
        }

        return transferred;
    }
}
