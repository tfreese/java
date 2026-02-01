// Created: 12 Apr. 2025
package de.freese.gradle.cache.storage;

import java.io.InputStream;
import java.time.Instant;

import org.jspecify.annotations.Nullable;

/**
 * Storage API.
 *
 * @author Thomas Freese
 */
public interface Storage {

    @Nullable StorageEntry getStorageEntry(String key);

    /**
     * Locks the Key for parallel use.
     */
    void lock(String key);

    void put(String key, InputStream inputStream);

    void removeOlderThan(Instant instant);

    void unlock(String key);
}
