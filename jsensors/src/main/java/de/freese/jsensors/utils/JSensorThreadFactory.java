// Created: 10.09.2020
package de.freese.jsensors.utils;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class JSensorThreadFactory implements ThreadFactory {
    private final boolean daemon;
    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
    private final String namePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public JSensorThreadFactory(final String namePrefix) {
        this(namePrefix, true);
    }

    public JSensorThreadFactory(final String namePrefix, final boolean daemon) {
        super();

        this.namePrefix = Objects.requireNonNull(namePrefix, "namePrefix required") + "-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread = this.defaultThreadFactory.newThread(r);

        thread.setName(this.namePrefix + this.threadNumber.getAndIncrement());
        thread.setDaemon(this.daemon);

        return thread;
    }
}
