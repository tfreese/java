// Created: 04.10.2020
package de.freese.jsensors.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The {@link #get()}-Method blocks until {@link #setResponse(Object)} is called.
 *
 * @author Thomas Freese
 */
public class SyncFuture<T> implements Future<T> {
    private final CountDownLatch latch = new CountDownLatch(1);

    private T response;

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        this.latch.await();

        return this.response;
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.latch.await(timeout, unit)) {
            return this.response;
        }

        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.response != null;
    }

    public void setResponse(final T response) {
        this.response = response;

        this.latch.countDown();
    }
}
