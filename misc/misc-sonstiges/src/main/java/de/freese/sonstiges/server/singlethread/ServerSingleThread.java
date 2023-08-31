// Created: 31.10.2016
package de.freese.sonstiges.server.singlethread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Semaphore;

import de.freese.sonstiges.NamedThreadFactory;
import de.freese.sonstiges.server.AbstractServer;
import de.freese.sonstiges.server.ServerMain;

/**
 * Der Server kümmert sich um alle Verbindungen in einem einzelnen Thread.
 *
 * @author Thomas Freese
 */
public class ServerSingleThread extends AbstractServer {
    private final SelectorProvider selectorProvider;
    /**
     * ReentrantLock nicht möglich, da dort die Locks auf Thread-Ebene verwaltet werden.
     */
    private final Semaphore stopLock = new Semaphore(1, true);

    private boolean isShutdown;

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public ServerSingleThread(final int port) throws IOException {
        this(port, SelectorProvider.provider());
    }

    public ServerSingleThread(final int port, final SelectorProvider selectorProvider) throws IOException {
        super(port);

        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");
    }

    @Override
    public void run() {
        getLogger().info("starting '{}' on port: {}", getName(), getPort());

        Objects.requireNonNull(getIoHandler(), "ioHandler required");

        try {
            this.selector = this.selectorProvider.openSelector();

            // this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel = this.selectorProvider.openServerSocketChannel();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            // this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true); // Wird nicht von jedem OS unterstützt.
            this.serverSocketChannel.bind(new InetSocketAddress(getPort()), 50);

            // ServerSocket socket = this.serverSocketChannel.socket();
            // socket.setReuseAddress(true);
            // socket.bind(new InetSocketAddress(getPort()), 50);

            // SelectionKey selectionKey =
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            // selectionKey.attach(this);

            getLogger().info("'{}' listening on port: {}", getName(), getPort());

            this.stopLock.acquireUninterruptibly();
            getStartLock().release();

            while (!Thread.interrupted()) {
                int readyChannels = this.selector.select();

                if (this.isShutdown || !this.selector.isOpen()) {
                    break;
                }

                if (readyChannels > 0) {
                    Set<SelectionKey> selected = this.selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selected.iterator();

                    try {
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();
                            iterator.remove();

                            if (!selectionKey.isValid()) {
                                getLogger().debug("{}: SelectionKey not valid", ServerMain.getRemoteAddress(selectionKey));
                            }
                            else if (selectionKey.isAcceptable()) {
                                // Verbindung mit Client herstellen.
                                SocketChannel socketChannel = this.serverSocketChannel.accept();
                                socketChannel.configureBlocking(false);
                                socketChannel.register(this.selector, SelectionKey.OP_READ);

                                getLogger().debug("{}: Connection Accepted", socketChannel.getRemoteAddress());

                                // SelectionKey sk = socketChannel.register(this.selector, SelectionKey.OP_READ);
                                // sk.attach(obj)

                                // Selector aufwecken.
                                this.selector.wakeup();
                            }
                            else if (selectionKey.isConnectable()) {
                                getLogger().debug("{}: Client Connected", ServerMain.getRemoteAddress(selectionKey));
                            }
                            else if (selectionKey.isReadable()) {
                                getLogger().debug("{}: Read Request", ServerMain.getRemoteAddress(selectionKey));

                                // Request lesen.
                                getIoHandler().read(selectionKey);
                            }
                            else if (selectionKey.isWritable()) {
                                getLogger().debug("{}: Write Response", ServerMain.getRemoteAddress(selectionKey));

                                // Response schreiben.
                                getIoHandler().write(selectionKey);
                            }
                        }
                    }
                    finally {
                        selected.clear();
                    }
                }
            }
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
        finally {
            this.stopLock.release();
        }

        getLogger().info("'{}' stopped on port: {}", getName(), getPort());
    }

    @Override
    public void start() {
        Thread thread = new NamedThreadFactory(getName() + "-%d").newThread(this);
        thread.start();

        // Warten bis fertig.
        // this.startLock.acquireUninterruptibly();
        // this.startLock.release();
    }

    @Override
    public void stop() {
        getLogger().info("stopping '{}' on port: {}", getName(), getPort());

        this.isShutdown = true;
        this.selector.wakeup();

        // Warten bis Thread beendet.
        this.stopLock.acquireUninterruptibly();

        try {
            SelectionKey selectionKey = this.serverSocketChannel.keyFor(this.selector);

            if (selectionKey != null) {
                selectionKey.cancel();
            }

            if (this.selector.isOpen()) {
                this.selector.close();
            }

            this.serverSocketChannel.close();
        }
        catch (IOException ex) {
            getLogger().error(ex.getMessage(), ex);
        }
        finally {
            this.stopLock.release();
        }
    }
}
