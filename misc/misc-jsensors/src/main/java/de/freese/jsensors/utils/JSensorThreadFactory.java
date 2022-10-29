// Created: 10.09.2020
package de.freese.jsensors.utils;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class JSensorThreadFactory implements ThreadFactory
{
    /**
    *
    */
    private final boolean daemon;
    /**
     *
     */
    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
    /**
     *
     */
    private final String namePrefix;
    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Erstellt ein neues {@link JSensorThreadFactory} Object.
     *
     * @param namePrefix String
     */
    public JSensorThreadFactory(final String namePrefix)
    {
        this(namePrefix, true);
    }

    /**
     * Erstellt ein neues {@link JSensorThreadFactory} Object.
     *
     * @param namePrefix String
     * @param daemon boolean
     */
    public JSensorThreadFactory(final String namePrefix, final boolean daemon)
    {
        super();

        this.namePrefix = Objects.requireNonNull(namePrefix, "namePrefix required") + "-";
        this.daemon = daemon;
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r)
    {
        Thread thread = this.defaultThreadFactory.newThread(r);

        thread.setName(this.namePrefix + this.threadNumber.getAndIncrement());
        thread.setDaemon(this.daemon);

        return thread;
    }
}
