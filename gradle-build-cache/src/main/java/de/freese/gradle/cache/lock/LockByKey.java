// Created: 13 Apr. 2025
package de.freese.gradle.cache.lock;

/**
 * <a href="https://www.baeldung.com/java-acquire-lock-by-key">java-acquire-lock-by-key</a>
 *
 * @author Thomas Freese
 */
public interface LockByKey {
    void lock(String key);

    void unlock(String key);
}
