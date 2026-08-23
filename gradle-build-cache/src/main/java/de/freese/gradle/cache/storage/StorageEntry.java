package de.freese.gradle.cache.storage;

import java.io.OutputStream;

/**
 * @author Thomas Freese
 * @since 19.04.2025
 */
public interface StorageEntry {
    long getContentLength();

    String getKey();

    void transferTo(OutputStream outputStream);
}
