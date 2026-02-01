// Created: 12 Apr. 2025
package de.freese.gradle.cache.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

/**
 * File implementation for {@link Storage}-API.
 *
 * @author Thomas Freese
 */
public final class FileStorage extends AbstractStorage {
    private record FileStorageEntry(String key, Path path) implements StorageEntry {
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof FileStorageEntry(String k, Path p))) {
                return false;
            }

            return Objects.equals(key, k) && Objects.equals(path, p);
        }

        @Override
        public long getContentLength() {
            try {
                return Files.size(path());
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public String getKey() {
            return key();
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, path);
        }

        @Override
        public void transferTo(final OutputStream outputStream) {
            try (InputStream inputStream = Files.newInputStream(path());
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
                bufferedInputStream.transferTo(outputStream);

                // outputStream.flush();
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    private final Path workingDirectory;

    public FileStorage(final Path workingDirectory) {
        super();

        this.workingDirectory = Objects.requireNonNull(workingDirectory, "workingDirectory required");

        try {
            if (!Files.exists(workingDirectory)) {
                getLogger().info("create directory: {}", workingDirectory);

                Files.createDirectories(workingDirectory);
            }
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    @Nullable
    public StorageEntry getStorageEntry(final String key) {
        Objects.requireNonNull(key, "key required");

        final Path path = workingDirectory.resolve(key);

        if (!Files.exists(path)) {
            return null;
        }

        return new FileStorageEntry(key, path);
    }

    @Override
    public void put(final String key, final InputStream inputStream) {
        final Path path = workingDirectory.resolve(key);

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            inputStream.transferTo(bufferedOutputStream);

            bufferedOutputStream.flush();
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void removeOlderThan(final Instant instant) {
        try {
            final List<Path> files = getFiles();

            getLogger().info(toString(files));

            final AtomicInteger counter = new AtomicInteger(0);

            for (Path file : files) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }

                final FileTime fileTime = Files.getLastModifiedTime(file);

                if (fileTime.toInstant().isBefore(instant) && remove(file.getFileName().toString())) {
                    counter.incrementAndGet();
                }
            }

            getLogger().info("Entries removed: {}", counter);
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public String toString() {
        return toString(getFiles());
    }

    @Override
    protected boolean remove(final String key) {
        final Path path = workingDirectory.resolve(key);

        lock(key);

        try {
            final boolean exist = Files.exists(path);

            Files.delete(path);

            return exist;
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        finally {
            unlock(key);
        }
    }

    private List<Path> getFiles() {
        try (Stream<Path> paths = Files.walk(workingDirectory)) {
            return paths.filter(Files::isRegularFile).sorted().toList();
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String toString(final List<Path> files) {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" [");
        sb.append("workingDirectory=").append(workingDirectory);
        sb.append(", files=").append(files.size());
        sb.append(']');

        return sb.toString();
    }
}
