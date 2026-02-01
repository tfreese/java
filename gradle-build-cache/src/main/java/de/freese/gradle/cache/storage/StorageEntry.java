// Created: 19 Apr. 2025
package de.freese.gradle.cache.storage;

import java.io.OutputStream;

/**
 * Storage Entry.
 *
 * @author Thomas Freese
 */
public interface StorageEntry {
    long getContentLength();

    String getKey();

    void transferTo(OutputStream outputStream);
}
