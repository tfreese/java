// Created: 12 Apr. 2025
package de.freese.gradle.cache.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.jspecify.annotations.Nullable;

/**
 * {@link Caffeine} implementation for {@link Storage}-API.
 *
 * @author Thomas Freese
 */
public final class CaffeineStorage extends AbstractStorage {
    private record CaffeineStorageHandle(String key, byte[] bytes) implements StorageEntry {
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof CaffeineStorageHandle(String k, byte[] v))) {
                return false;
            }
            return Objects.equals(key(), k) && Objects.deepEquals(bytes(), v);
        }

        @Override
        public long getContentLength() {
            return bytes().length;
        }

        @Override
        public String getKey() {
            return key();
        }

        @Override
        public int hashCode() {
            return Objects.hash(key(), Arrays.hashCode(bytes()));
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
            sb.append(" [");
            sb.append("bytes=").append(Arrays.toString(bytes()));
            sb.append(']');

            return sb.toString();
        }

        @Override
        public void transferTo(final OutputStream outputStream) {
            try {
                outputStream.write(bytes);

                // outputStream.flush();
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    private final Cache<String, byte[]> cache;

    public CaffeineStorage(final Duration expireAfterWrite, final ScheduledExecutorService scheduledExecutorService, final ExecutorService executorService) {
        super();

        if (expireAfterWrite == null || expireAfterWrite.isZero() || expireAfterWrite.isNegative()) {
            throw new IllegalArgumentException("expireAfterWrite must be positive: " + expireAfterWrite);
        }

        Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService required");
        Objects.requireNonNull(executorService, "executorService required");

        cache = Caffeine.newBuilder()
                .maximumSize(Integer.MAX_VALUE)
                .expireAfterWrite(expireAfterWrite)
                .recordStats()
                .executor(executorService)
                .scheduler(Scheduler.forScheduledExecutorService(scheduledExecutorService))
                // .evictionListener((key, value, cause) -> LOGGER.info("Eviction: {} - {}", cause, key))
                .removalListener((key, value, cause) -> getLogger().info("Removal: {} - {}", cause, key))
                .build();
    }

    @Override
    @Nullable
    public StorageEntry getStorageEntry(final String key) {
        Objects.requireNonNull(key, "key required");

        final byte[] bytes = cache.getIfPresent(key);

        if (bytes == null) {
            return null;
        }

        return new CaffeineStorageHandle(key, bytes);
    }

    @Override
    public void put(final String key, final InputStream inputStream) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            inputStream.transferTo(baos);

            baos.flush();

            cache.put(key, baos.toByteArray());
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void removeOlderThan(final Instant instant) {
        // Handles by Caffeine.expireAfterWrite(Duration).
        getLogger().info(toString());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" with ").append(cache.estimatedSize()).append(" entries");

        final CacheStats cacheStats = cache.stats();
        sb.append(": hitRate=").append(cacheStats.hitRate());
        sb.append(", missRate=").append(cacheStats.missRate());
        sb.append(", evictionCount=").append(cacheStats.evictionCount());

        return sb.toString();
    }

    @Override
    protected boolean remove(final String key) {
        lock(key);

        try {
            final boolean exist = cache.getIfPresent(key) != null;

            cache.invalidate(key);

            return exist;
        }
        finally {
            unlock(key);
        }
    }
}
