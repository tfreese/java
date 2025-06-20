// Created: 31.10.2016
package de.freese.sonstiges.server.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import de.freese.sonstiges.NamedThreadFactory;
import de.freese.sonstiges.server.AbstractServer;

/**
 * Der Server kümmert sich um alle Verbindungen in separaten Threads.
 *
 * @author Thomas Freese
 */
public class ServerAsync extends AbstractServer {
    public static void close(final AsynchronousSocketChannel channel, final Logger logger) {
        try {
            channel.shutdownInput();
            channel.shutdownOutput();
            channel.close();
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private final AsynchronousChannelGroup channelGroup;

    private AsynchronousServerSocketChannel serverSocketChannel;

    public ServerAsync(final int port) throws IOException {
        this(port, 3);
    }

    public ServerAsync(final int port, final AsynchronousChannelGroup channelGroup) {
        super(port);

        this.channelGroup = Objects.requireNonNull(channelGroup, "channelGroup required");
    }

    public ServerAsync(final int port, final int poolSize) throws IOException {
        this(port, AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(poolSize, new NamedThreadFactory("worker-%d"))));
    }

    @Override
    public void run() {
        getLogger().info("starting '{}' on port: {}", getName(), getPort());

        try {
            // serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            // serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true); // Wird nicht von jedem OS unterstützt.
            serverSocketChannel.bind(new InetSocketAddress(getPort()), 50);

            getLogger().info("'{}' listening on port: {}", getName(), getPort());
            getStartLock().release();

            accept();
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    @Override
    public void start() {
        run();

        // Wait till finished.
        // startLock.acquireUninterruptibly();
        // startLock.release();
    }

    @Override
    public void stop() {
        getLogger().info("stopping '{}' on port: {}", getName(), getPort());

        shutdown(channelGroup, getLogger());

        getLogger().info("'{}' stopped on port: {}", getName(), getPort());
    }

    /**
     * Wartet auf neue Connections.
     */
    private void accept() {
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(final AsynchronousSocketChannel channel, final Void attachment) {
                try {
                    getLogger().debug("{}: Connection Accepted", channel.getRemoteAddress());
                }
                catch (IOException ex) {
                    failed(ex, null);
                }

                // Transfer next Request to another Tread.
                accept();

                // Transfer READ-Operation to another Thread.
                read(channel, ByteBuffer.allocate(256));
            }

            @Override
            public void failed(final Throwable ex, final Void attachment) {
                getLogger().error(ex.getMessage(), ex);
            }
        });
    }

    private void read(final AsynchronousSocketChannel channel, final ByteBuffer byteBuffer) {
        final MyAttachment attachment = new MyAttachment(byteBuffer, channel);

        channel.read(byteBuffer, attachment, new HttpReadHandler());
    }

    private void shutdown(final AsynchronousChannelGroup channelGroup, final Logger logger) {
        logger.debug("shutdown AsynchronousChannelGroup");
        channelGroup.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!channelGroup.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("Timed out while waiting for AsynchronousChannelGroup");

                // Cancel currently executing tasks.
                channelGroup.shutdownNow();

                // Wait a while for tasks to respond to being canceled.
                if (!channelGroup.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.error("AsynchronousChannelGroup did not terminate");
                }
            }
        }
        catch (InterruptedException | IOException ex) {
            // (Re-)Cancel if current thread also interrupted.
            try {
                channelGroup.shutdownNow();
            }
            catch (IOException ex2) {
                logger.error("AsynchronousChannelGroup did not terminate");
            }

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }
}
