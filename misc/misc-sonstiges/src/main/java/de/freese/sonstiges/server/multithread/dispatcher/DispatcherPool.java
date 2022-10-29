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

import de.freese.sonstiges.NamedThreadFactory;
import de.freese.sonstiges.server.handler.IoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Der {@link Dispatcher} kümmert sich um das Connection-Handling der Clients nach dem 'accept'.<br>
 *
 * @author Thomas Freese
 */
public class DispatcherPool implements Dispatcher
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherPool.class);
    /**
     *
     */
    private static final AtomicIntegerFieldUpdater<DispatcherPool> NEXT_INDEX = AtomicIntegerFieldUpdater.newUpdater(DispatcherPool.class, "nextIndex");
    /**
     *
     */
    private final List<DefaultDispatcher> dispatchers;
    /**
     *
     */
    private final int numOfDispatcher;
    /**
     *
     */
    private final int numOfWorker;
    /**
     *
     */
    volatile int nextIndex;
    /**
     *
     */
    private ExecutorService executorServiceWorker;

    /**
     * Erstellt ein neues {@link DispatcherPool} Object.
     *
     * @param numOfDispatcher int
     * @param numOfWorker int
     */
    public DispatcherPool(final int numOfDispatcher, final int numOfWorker)
    {
        super();

        if (numOfDispatcher < 1)
        {
            throw new IllegalArgumentException("numOfDispatcher < 1: " + numOfDispatcher);
        }

        if (numOfWorker < 1)
        {
            throw new IllegalArgumentException("numOfWorker < 1: " + numOfWorker);
        }

        if (numOfDispatcher > numOfWorker)
        {
            String message = String.format("numOfDispatcher > numOfWorker: %d < %d", numOfDispatcher, numOfWorker);
            throw new IllegalArgumentException(message);
        }

        this.numOfDispatcher = numOfDispatcher;
        this.numOfWorker = numOfWorker;

        this.dispatchers = new ArrayList<>(numOfDispatcher);
    }

    /**
     * @see de.freese.sonstiges.server.multithread.dispatcher.Dispatcher#register(java.nio.channels.SocketChannel)
     */
    @Override
    public synchronized void register(final SocketChannel socketChannel)
    {
        nextDispatcher().register(socketChannel);
    }

    /**
     * @param ioHandler {@link IoHandler}
     * @param selectorProvider {@link SelectorProvider}
     * @param serverName String
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void start(final IoHandler<SelectionKey> ioHandler, final SelectorProvider selectorProvider, final String serverName) throws Exception
    {
        ThreadFactory threadFactoryDispatcher = new NamedThreadFactory(serverName + "-dispatcher-%d");
        ThreadFactory threadFactoryWorker = new NamedThreadFactory(serverName + "-worker-%d");

        // this.executorServiceWorker = new ThreadPoolExecutor(1, this.numOfWorker, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactoryWorker);
        this.executorServiceWorker = Executors.newFixedThreadPool(this.numOfWorker, threadFactoryWorker);

        while (this.dispatchers.size() < this.numOfDispatcher)
        {
            DefaultDispatcher dispatcher = new DefaultDispatcher(selectorProvider.openSelector(), ioHandler, this.executorServiceWorker);
            this.dispatchers.add(dispatcher);

            Thread thread = threadFactoryDispatcher.newThread(dispatcher);

            getLogger().debug("start dispatcher: {}", thread.getName());
            thread.start();
        }
    }

    /**
     *
     */
    public void stop()
    {
        this.dispatchers.forEach(DefaultDispatcher::stop);
        this.executorServiceWorker.shutdown();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * Liefert den nächsten {@link Dispatcher} im Round-Robin Verfahren.<br>
     *
     * @return {@link Dispatcher}
     */
    private synchronized Dispatcher nextDispatcher()
    {
        int length = this.dispatchers.size();

        int indexToUse = Math.abs(NEXT_INDEX.getAndIncrement(this) % length);

        return this.dispatchers.get(indexToUse);
    }
}
