// Created: 10.09.2020
package de.freese.sonstiges.server.multithread.dispatcher;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.server.handler.IoHandler;

/**
 * The {@link Dispatcher} handles the Client Connections after the 'accept'.<br>
 *
 * @author Thomas Freese
 */
public class DispatcherPool implements Dispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherPool.class);
    private static final AtomicIntegerFieldUpdater<DispatcherPool> NEXT_INDEX = AtomicIntegerFieldUpdater.newUpdater(DispatcherPool.class, "nextIndex");

    private final List<DefaultDispatcher> dispatchers;
    private final int numOfDispatcher;
    private final int numOfWorker;

    private ExecutorService executorServiceWorker;
    private volatile int nextIndex;

    public DispatcherPool(final int numOfDispatcher, final int numOfWorker) {
        super();

        if (numOfDispatcher < 1) {
            throw new IllegalArgumentException("numOfDispatcher < 1: " + numOfDispatcher);
        }

        if (numOfWorker < 1) {
            throw new IllegalArgumentException("numOfWorker < 1: " + numOfWorker);
        }

        if (numOfDispatcher > numOfWorker) {
            final String message = String.format("numOfDispatcher > numOfWorker: %d < %d", numOfDispatcher, numOfWorker);
            throw new IllegalArgumentException(message);
        }

        this.numOfDispatcher = numOfDispatcher;
        this.numOfWorker = numOfWorker;

        dispatchers = new ArrayList<>(numOfDispatcher);
    }

    @Override
    public synchronized void register(final SocketChannel socketChannel) {
        nextDispatcher().register(socketChannel);
    }

    public void start(final IoHandler<SelectionKey> ioHandler, final SelectorProvider selectorProvider, final String serverName) throws Exception {
        // final ThreadFactory threadFactoryDispatcher = new NamedThreadFactory(serverName + "-dispatcher-%d");
        // final ThreadFactory threadFactoryWorker = new NamedThreadFactory(serverName + "-worker-%d");
        final ThreadFactory threadFactoryDispatcher = Thread.ofPlatform().daemon().name(serverName + "-dispatcher-", 1).factory();
        final ThreadFactory threadFactoryWorker = Thread.ofPlatform().daemon().name(serverName + "-worker-", 1).factory();

        // executorServiceWorker = new ThreadPoolExecutor(1, numOfWorker, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactoryWorker);
        executorServiceWorker = Executors.newFixedThreadPool(numOfWorker, threadFactoryWorker);

        while (dispatchers.size() < numOfDispatcher) {
            final DefaultDispatcher dispatcher = new DefaultDispatcher(selectorProvider.openSelector(), ioHandler, executorServiceWorker);
            dispatchers.add(dispatcher);

            final Thread thread = threadFactoryDispatcher.newThread(dispatcher);

            getLogger().debug("start dispatcher: {}", thread.getName());
            thread.start();
        }
    }

    public void stop() {
        dispatchers.forEach(DefaultDispatcher::stop);
        executorServiceWorker.shutdown();
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns the next {@link Dispatcher} in a Round-Robin procedure.<br>
     */
    private synchronized Dispatcher nextDispatcher() {
        final int length = dispatchers.size();

        final int indexToUse = Math.abs(NEXT_INDEX.getAndIncrement(this) % length);

        return dispatchers.get(indexToUse);
    }
}
