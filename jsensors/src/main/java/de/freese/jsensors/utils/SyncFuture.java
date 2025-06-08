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
        latch.await();

        return response;
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return response;
        }

        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    public void setResponse(final T response) {
        this.response = response;

        latch.countDown();
    }
}
