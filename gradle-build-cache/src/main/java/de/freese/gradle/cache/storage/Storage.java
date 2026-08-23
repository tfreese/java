package de.freese.gradle.cache.storage;

import java.io.InputStream;
import java.time.Instant;

import org.jspecify.annotations.Nullable;

/**
 * Storage API.
 *
 * @author Thomas Freese
 * @since 12.04.2025
 */
public interface Storage {
    @Nullable StorageEntry getEntry(String key);

    /**
     * Locks the Key for parallel use.
     */
    void lock(String key);

    void put(String key, InputStream inputStream);

    void removeOlderThan(Instant instant);

    void unlock(String key);
}
