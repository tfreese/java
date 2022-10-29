// Created: 21.09.2019
package de.freese.maven.proxy.netty;

import java.util.Objects;
import java.util.concurrent.Executor;

import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.repository.Repository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class MavenProxyNetty implements MavenProxy
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyNetty.class);
    /**
     *
     */
    private EventLoopGroup acceptorGroup;
    /**
     *
     */
    private Executor executor;
    /**
     *
     */
    private int port = -1;
    /**
     *
     */
    private Repository repository;
    /**
     *
     */
    private EventLoopGroup workerGroup;

    /**
     * @see de.freese.maven.proxy.MavenProxy#setExecutor(java.util.concurrent.Executor)
     */
    @Override
    public void setExecutor(final Executor executor)
    {
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#setPort(int)
     */
    @Override
    public void setPort(final int port)
    {
        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0");
        }

        this.port = port;
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#setRepository(de.freese.maven.proxy.repository.Repository)
     */
    @Override
    public void setRepository(final Repository repository)
    {
        this.repository = Objects.requireNonNull(repository, "repository required");
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#start()
     */
    @Override
    public void start()
    {
        Objects.requireNonNull(this.repository, "repository required");
        Objects.requireNonNull(this.executor, "executor required");

        if (this.port <= 0)
        {
            throw new IllegalArgumentException("port <= 0");
        }

        LOGGER.info("starting MavenProxy at Port {}", this.port);

        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();

            this.acceptorGroup = new NioEventLoopGroup(2, this.executor);
            this.workerGroup = new NioEventLoopGroup(6, this.executor);

            // @formatter:off
            bootstrap.group(this.acceptorGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new NettyMavenInitializer(this.repository));
            // @formatter:on

            ChannelFuture ch = bootstrap.bind(this.port);

            ch.channel().closeFuture().sync();
        }
        catch (RuntimeException ex)
        {
            LOGGER.error(ex.getMessage(), ex);

            throw ex;
        }
        catch (Exception ex)
        {
            LOGGER.error(ex.getMessage(), ex);

            throw new RuntimeException(ex);
        }
        // finally
        // {
        // stop()
        // }
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#stop()
     */
    @Override
    public void stop()
    {
        LOGGER.info("stopping MavenProxy at Port {}", this.port);

        if (this.acceptorGroup != null)
        {
            this.acceptorGroup.shutdownGracefully();
        }

        if (this.workerGroup != null)
        {
            this.workerGroup.shutdownGracefully();
        }
    }
}
