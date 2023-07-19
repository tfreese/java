// Created: 21.09.2019
package de.freese.maven.proxy.netty;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.repository.EmptyRepository;
import de.freese.maven.proxy.repository.Repository;

/**
 * <a href="https://github.com/codyebberson/netty-example/blob/master/src/main/java/nettyexample/server/WebServer.java">netty-webserver</a>
 *
 * @author Thomas Freese
 */
public class MavenProxyNetty implements MavenProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyNetty.class);

    public static void main(String[] args) {
        MavenProxyNetty mavenProxyNetty = new MavenProxyNetty();
        mavenProxyNetty.setPort(9999);
        mavenProxyNetty.setExecutor(Executors.newCachedThreadPool());
        mavenProxyNetty.setRepository(new EmptyRepository());

        mavenProxyNetty.start();

        //        mavenProxyNetty.stop();
    }

    private EventLoopGroup acceptorGroup;

    private Executor executor;

    private int port = -1;

    private Repository repository;

    private EventLoopGroup workerGroup;

    @Override
    public void setExecutor(final Executor executor) {
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    @Override
    public void setPort(final int port) {
        if (port <= 0) {
            throw new IllegalArgumentException("port <= 0");
        }

        this.port = port;
    }

    @Override
    public void setRepository(final Repository repository) {
        this.repository = Objects.requireNonNull(repository, "repository required");
    }

    @Override
    public void start() {
        Objects.requireNonNull(this.repository, "repository required");
        Objects.requireNonNull(this.executor, "executor required");

        if (this.port <= 0) {
            throw new IllegalArgumentException("port <= 0");
        }

        LOGGER.info("starting MavenProxy at Port {}", this.port);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            Class<? extends ServerChannel> serverSocketChannelClass;

            if (Epoll.isAvailable()) {
                this.acceptorGroup = new EpollEventLoopGroup(2, this.executor);
                this.workerGroup = new EpollEventLoopGroup(6, this.executor);
                serverSocketChannelClass = EpollServerSocketChannel.class;
            }
            else {
                this.acceptorGroup = new NioEventLoopGroup(2, this.executor);
                this.workerGroup = new NioEventLoopGroup(6, this.executor);
                serverSocketChannelClass = NioServerSocketChannel.class;
            }

            // @formatter:off
            bootstrap.group(this.acceptorGroup, this.workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .channel(serverSocketChannelClass)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyMavenInitializer(this.repository))
                    .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)
            ;
            // @formatter:on

            ChannelFuture ch = bootstrap.bind(this.port);

            ch.channel().closeFuture().sync();
        }
        catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);

            throw ex;
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);

            throw new RuntimeException(ex);
        }
        // finally
        // {
        // stop()
        // }
    }

    @Override
    public void stop() {
        LOGGER.info("stopping MavenProxy at Port {}", this.port);

        if (this.acceptorGroup != null) {
            this.acceptorGroup.shutdownGracefully();
        }

        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
    }
}
