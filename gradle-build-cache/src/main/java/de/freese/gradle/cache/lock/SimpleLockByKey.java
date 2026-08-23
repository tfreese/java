package de.freese.gradle.cache.lock;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrency handled by {@link ConcurrentHashMap}.
 *
 * @author Thomas Freese
 * @since 13.04.2025
 */
public final class SimpleLockByKey implements LockByKey {
    private static final Set<String> USED_KEYS = ConcurrentHashMap.newKeySet();

    @Override
    public void lock(final String key) {
        USED_KEYS.add(key);
    }

    @Override
    public void unlock(final String key) {
        USED_KEYS.remove(key);
    }
}
