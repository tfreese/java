// Created: 27.03.2018
package de.freese.maven.proxy.netty;

import java.util.Objects;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

import de.freese.maven.proxy.repository.Repository;

/**
 * {@link ChannelInitializer} für den Maven Proxy.<br>
 *
 * @author Thomas Freese
 */
public class NettyMavenInitializer extends ChannelInitializer<SocketChannel> {
    private final Repository repository;

    private final SslContext sslContext;

    public NettyMavenInitializer(final Repository repository) {
        super();

        this.repository = Objects.requireNonNull(repository, "repository required");
        this.sslContext = null;
    }

    /**
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (this.sslContext != null) {
            pipeline.addLast(this.sslContext.newHandler(ch.alloc()));
        }

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536)); // Keine Chunks
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new NettyMavenRequestHandler(this.repository));
    }
}
