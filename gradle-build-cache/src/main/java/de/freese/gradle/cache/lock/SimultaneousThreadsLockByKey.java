// Created: 13 Apr. 2025
package de.freese.gradle.cache.lock;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Concurrency handled by {@link Semaphore}.
 *
 * @author Thomas Freese
 */
public final class SimultaneousThreadsLockByKey implements LockByKey {
    private static final int ALLOWED_THREADS = 2;

    private static final Map<String, Semaphore> SEMAPHORES = new ConcurrentHashMap<>();

    @Override
    public void lock(final String key) {
        SEMAPHORES.computeIfAbsent(key, k -> new Semaphore(ALLOWED_THREADS)).acquireUninterruptibly();
    }

    @Override
    public void unlock(final String key) {
        final Semaphore semaphore = SEMAPHORES.get(key);

        Objects.requireNonNull(semaphore, "semaphore required");

        semaphore.release();

        if (semaphore.availablePermits() == ALLOWED_THREADS) {
            SEMAPHORES.remove(key);
        }
    }
}
