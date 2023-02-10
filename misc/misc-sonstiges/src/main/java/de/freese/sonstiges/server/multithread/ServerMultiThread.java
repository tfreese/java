// Created: 31.10.2016
package de.freese.sonstiges.server.multithread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Objects;

import de.freese.sonstiges.NamedThreadFactory;
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

    public ServerMultiThread(final int port, final int numOfDispatcher, final int numOfWorker) throws IOException {
        this(port, numOfDispatcher, numOfWorker, SelectorProvider.provider());
    }

    public ServerMultiThread(final int port, final int numOfDispatcher, final int numOfWorker, final SelectorProvider selectorProvider) throws IOException {
        super(port);

        this.dispatcherPool = new DispatcherPool(numOfDispatcher, numOfWorker);
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        getLogger().info("starting '{}' on port: {}", getName(), getPort());

        Objects.requireNonNull(getIoHandler(), "ioHandler required");

        try {
            // this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel = this.selectorProvider.openServerSocketChannel();
            this.serverSocketChannel.configureBlocking(false);

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.TCP_NODELAY)) {
                // this.serverSocketChannel.getOption(StandardSocketOptions.TCP_NODELAY);
                this.serverSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            }

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_REUSEADDR)) {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_REUSEADDR);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            }

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_REUSEPORT)) {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_REUSEPORT);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true);
            }

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_RCVBUF)) {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_RCVBUF);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
            }

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_SNDBUF)) {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_SNDBUF);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 64 * 1024);
            }

            this.serverSocketChannel.bind(new InetSocketAddress(getPort()), 50);

            // ServerSocket socket = this.serverSocketChannel.socket();
            // socket.setReuseAddress(true);
            // socket.bind(new InetSocketAddress(getPort()), 50);

            // Create Dispatcher.
            this.dispatcherPool.start(getIoHandler(), this.selectorProvider, getName());

            // Create Acceptor.
            this.acceptor = new Acceptor(this.selectorProvider.openSelector(), this.serverSocketChannel, this.dispatcherPool);

            Thread thread = new NamedThreadFactory(getName() + "-acceptor-%d").newThread(this.acceptor);
            getLogger().debug("start {}", thread.getName());
            thread.start();

            getLogger().info("'{}' listening on port: {}", getName(), getPort());
            getStartLock().release();
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * @see de.freese.sonstiges.server.AbstractServer#start()
     */
    @Override
    public void start() {
        run();

        // Wait if ready.
        // this.startLock.acquireUninterruptibly();
        // this.startLock.release();
    }

    /**
     * @see de.freese.sonstiges.server.AbstractServer#stop()
     */
    @Override
    public void stop() {
        getLogger().info("stopping '{}' on port: {}", getName(), getPort());

        this.acceptor.stop();
        this.dispatcherPool.stop();

        try {
            // SelectionKey selectionKey = this.serverSocketChannel.keyFor(this.selector);
            //
            // if (selectionKey != null)
            // {
            // selectionKey.cancel();
            // }

            this.serverSocketChannel.close();
        }
        catch (IOException ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        getLogger().info("'{}' stopped on port: {}", getName(), getPort());
    }
}
