// Created: 12 Apr. 2025
package de.freese.gradle.cache.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.Nullable;

/**
 * {@link Map} implementation for {@link Storage}-API.
 *
 * @author Thomas Freese
 */
public final class MapStorage extends AbstractStorage {
    private record MapStorageEntry(String key, byte[] bytes) implements StorageEntry {

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof MapStorageEntry(String k, byte[] v))) {
                return false;
            }

            return Objects.equals(key, k) && Objects.deepEquals(bytes, v);
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
            return Objects.hash(key, Arrays.hashCode(bytes));
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
            sb.append(" [");
            sb.append("bytes=").append(Arrays.toString(bytes));
            sb.append(']');

            return sb.toString();
        }

        @Override
        public void transferTo(final OutputStream outputStream) {
            try {
                outputStream.write(bytes());

                // outputStream.flush();
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    private record TimedValue(Instant instant, byte[] bytes) {
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof TimedValue(Instant instant1, byte[] value1))) {
                return false;
            }

            return Objects.deepEquals(bytes, value1) && Objects.equals(instant, instant1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(instant, Arrays.hashCode(bytes));
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
            sb.append(" [");
            sb.append("instant=").append(instant);
            sb.append(", bytes=").append(Arrays.toString(bytes));
            sb.append(']');

            return sb.toString();
        }
    }

    private final Map<String, TimedValue> cache = new ConcurrentHashMap<>();

    @Override
    @Nullable
    public StorageEntry getStorageEntry(final String key) {
        Objects.requireNonNull(key, "key required");

        final TimedValue timedValue = cache.get(key);

        if (timedValue == null) {
            return null;
        }

        return new MapStorageEntry(key, timedValue.bytes());
    }

    @Override
    public void put(final String key, final InputStream inputStream) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            inputStream.transferTo(baos);

            baos.flush();

            cache.put(key, new TimedValue(Instant.now(), baos.toByteArray()));
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void removeOlderThan(final Instant instant) {
        getLogger().info(toString());

        final AtomicInteger counter = new AtomicInteger(0);
        final Set<String> keys = Set.copyOf(cache.keySet());

        keys.forEach(key -> {
            final TimedValue timedValue = cache.get(key);

            if (timedValue == null) {
                return;
            }

            if (timedValue.instant().isBefore(instant) && remove(key)) {
                counter.incrementAndGet();
            }
        });

        getLogger().info("Entries removed: {}", counter);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" with ").append(cache.size()).append(" entries");

        return sb.toString();
    }

    @Override
    protected boolean remove(final String key) {
        lock(key);

        try {
            return cache.remove(key) != null;
        }
        finally {
            unlock(key);
        }
    }
}
