// Created: 13 Apr. 2025
package de.freese.gradle.cache.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.gradle.cache.lock.LockByKey;
import de.freese.gradle.cache.lock.ReentrantLockByKey;

/**
 * @author Thomas Freese
 */
abstract class AbstractStorage implements Storage {
    private final LockByKey lockByKey = new ReentrantLockByKey();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void lock(final String key) {
        lockByKey.lock(key);
    }

    @Override
    public void unlock(final String key) {
        lockByKey.unlock(key);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected abstract boolean remove(String key);
}
