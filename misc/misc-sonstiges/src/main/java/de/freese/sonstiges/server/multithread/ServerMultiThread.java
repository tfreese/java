// Created: 31.10.2016
package de.freese.sonstiges.server.multithread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Objects;

import de.freese.sonstiges.server.AbstractServer;
import de.freese.sonstiges.server.handler.IoHandler;
import de.freese.sonstiges.server.multithread.dispatcher.Dispatcher;
import de.freese.sonstiges.server.multithread.dispatcher.DispatcherPool;

/**
 * These Server is working by the Acceptor-Reactor Pattern.<br>
 * The {@link Acceptor} handles the Client-Connections and delegate them to the {@link Dispatcher}.<br>
 * The {@link Dispatcher} handles the Client Connections after the 'accept'.<br>
 * The {@link IoHandler} handles the Request and Response in a separate Thread.<br>
 *
 * @author Thomas Freese
 */
public class ServerMultiThread extends AbstractServer {
    private final DispatcherPool dispatcherPool;
    private final SelectorProvider selectorProvider;

    private Acceptor acceptor;
    private ServerSocketChannel serverSocketChannel;

    public ServerMultiThread(final int port, final int numOfDispatcher, final int numOfWorker) {
        this(port, numOfDispatcher, numOfWorker, SelectorProvider.provider());
    }

    public ServerMultiThread(final int port, final int numOfDispatcher, final int numOfWorker, final SelectorProvider selectorProvider) {
        super(port);

        this.dispatcherPool = new DispatcherPool(numOfDispatcher, numOfWorker);
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");
    }

    @Override
    public void run() {
        getLogger().info("starting '{}' on port: {}", getName(), getPort());

        Objects.requireNonNull(getIoHandler(), "ioHandler required");

        try {
            // this.serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel = selectorProvider.openServerSocketChannel();
            serverSocketChannel.configureBlocking(false);

            if (serverSocketChannel.supportedOptions().contains(StandardSocketOptions.TCP_NODELAY)) {
                // serverSocketChannel.getOption(StandardSocketOptions.TCP_NODELAY);
                serverSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            }

            if (serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_REUSEADDR)) {
                // serverSocketChannel.getOption(StandardSocketOptions.SO_REUSEADDR);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            }

            if (serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_REUSEPORT)) {
                // serverSocketChannel.getOption(StandardSocketOptions.SO_REUSEPORT);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true);
            }

            if (serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_RCVBUF)) {
                // serverSocketChannel.getOption(StandardSocketOptions.SO_RCVBUF);
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
            }

            if (serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_SNDBUF)) {
                // serverSocketChannel.getOption(StandardSocketOptions.SO_SNDBUF);
                serverSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 64 * 1024);
            }

            serverSocketChannel.bind(new InetSocketAddress(getPort()), 50);

            // ServerSocket socket = serverSocketChannel.socket();
            // socket.setReuseAddress(true);
            // socket.bind(new InetSocketAddress(getPort()), 50);

            // Create Dispatcher.
            dispatcherPool.start(getIoHandler(), selectorProvider, getName());

            // Create Acceptor.
            acceptor = new Acceptor(selectorProvider.openSelector(), serverSocketChannel, dispatcherPool);

            // final Thread thread = new NamedThreadFactory(getName() + "-acceptor-%d").newThread(acceptor);
            final Thread thread = Thread.ofPlatform().daemon().name(getName() + "-acceptor-", 1).factory().newThread(acceptor);
            getLogger().debug("start {}", thread.getName());
            thread.start();

            getLogger().info("'{}' listening on port: {}", getName(), getPort());
            getStartLock().release();
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    @Override
    public void start() {
        run();

        // Wait if ready.
        // startLock.acquireUninterruptibly();
        // startLock.release();
    }

    @Override
    public void stop() {
        getLogger().info("stopping '{}' on port: {}", getName(), getPort());

        acceptor.stop();
        dispatcherPool.stop();

        try {
            // final SelectionKey selectionKey = serverSocketChannel.keyFor(selector);
            //
            // if (selectionKey != null) {
            // selectionKey.cancel();
            // }

            serverSocketChannel.close();
        }
        catch (IOException ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        getLogger().info("'{}' stopped on port: {}", getName(), getPort());
    }
}
