// Created: 13 Apr. 2025
package de.freese.gradle.cache.lock;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrency handled by {@link ReentrantLock}.
 *
 * @author Thomas Freese
 */
public final class ReentrantLockByKey implements LockByKey {
    private static final Map<String, LockWrapper> LOCKS = new ConcurrentHashMap<>();

    private static final class LockWrapper {
        private final Lock lock = new ReentrantLock(true);
        private final AtomicInteger numberOfThreadsInQueue = new AtomicInteger(0);

        private LockWrapper addThreadInQueue() {
            numberOfThreadsInQueue.incrementAndGet();

            return this;
        }

        private int removeThreadFromQueue() {
            return numberOfThreadsInQueue.decrementAndGet();
        }
    }

    @Override
    public void lock(final String key) {
        final LockWrapper lockWrapper = LOCKS.computeIfAbsent(key, k -> new LockWrapper()).addThreadInQueue();
        lockWrapper.lock.lock();
    }

    @Override
    public void unlock(final String key) {
        final LockWrapper lockWrapper = LOCKS.get(key);

        Objects.requireNonNull(lockWrapper, "lockWrapper required");

        lockWrapper.lock.unlock();

        if (lockWrapper.removeThreadFromQueue() == 0) {
            // We pass in the specific value to remove to Entry the case where another thread would queue right before the removal.
            LOCKS.remove(key);
        }
    }
}
