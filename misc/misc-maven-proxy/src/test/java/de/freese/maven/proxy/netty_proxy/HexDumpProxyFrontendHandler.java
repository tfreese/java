// Created: 25.09.2019
package de.freese.maven.proxy.netty_proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;

/**
 * @author Thomas Freese
 */
public class HexDumpProxyFrontendHandler extends ChannelInboundHandlerAdapter
{
    /**
     * Closes the specified channel after all queued write requests are flushed.
     *
     * @param ch {@link Channel}
     */
    static void closeOnFlush(final Channel ch)
    {
        if (ch.isActive())
        {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * As we use inboundChannel.eventLoop() when building the Bootstrap this does not need to be volatile as the outboundChannel will use the same EventLoop
     * (and therefore Thread) as the inboundChannel.
     */
    private Channel outboundChannel;
    /**
     *
     */
    private final String remoteHost;
    /**
     *
     */
    private final int remotePort;

    /**
     * Erstellt ein neues {@link HexDumpProxyFrontendHandler} Object.
     *
     * @param remoteHost String
     * @param remotePort String
     */
    public HexDumpProxyFrontendHandler(final String remoteHost, final int remotePort)
    {
        super();

        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    {
        final Channel inboundChannel = ctx.channel();

        // Start the connection attempt.
        Bootstrap bootstrap = new Bootstrap();

        // @formatter:off
        bootstrap.group(inboundChannel.eventLoop())
            .channel(ctx.channel().getClass())
            .handler(new HexDumpProxyBackendHandler(inboundChannel))
            .option(ChannelOption.AUTO_READ, false);
        // @formatter:on

        ChannelFuture channelFuture = bootstrap.connect(this.remoteHost, this.remotePort);
        this.outboundChannel = channelFuture.channel();

        channelFuture.addListener(future -> {
            if (future.isSuccess())
            {
                // connection complete start to read first data
                inboundChannel.read();
            }
            else
            {
                // Close the connection if the connection attempt has failed.
                inboundChannel.close();
            }
        });
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelInactive(final ChannelHandlerContext ctx)
    {
        if (this.outboundChannel != null)
        {
            closeOnFlush(this.outboundChannel);
        }
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg)
    {
        if (this.outboundChannel.isActive())
        {
            // ChannelFutureListener
            this.outboundChannel.writeAndFlush(msg).addListener(future -> {
                if (future.isSuccess())
                {
                    // was able to flush out data, start to read the next chunk
                    ctx.channel().read();
                }
                else
                {
                    ((ChannelFuture) future).channel().close();
                }
            });
        }
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
    {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }
}