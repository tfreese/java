// Created: 25.09.2019
package de.freese.maven.proxy.netty_proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Thomas Freese
 */
public class HexDumpProxyBackendHandler extends ChannelInboundHandlerAdapter
{
    /**
     *
     */
    private final Channel inboundChannel;

    /**
     * Erstellt ein neues {@link HexDumpProxyBackendHandler} Object.
     *
     * @param inboundChannel {@link Channel}
     */
    public HexDumpProxyBackendHandler(final Channel inboundChannel)
    {
        super();

        this.inboundChannel = inboundChannel;
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    {
        ctx.read();
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelInactive(final ChannelHandlerContext ctx)
    {
        HexDumpProxyFrontendHandler.closeOnFlush(this.inboundChannel);
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg)
    {
        // ChannelFutureListener
        this.inboundChannel.writeAndFlush(msg).addListener(future ->
        {
            if (future.isSuccess())
            {
                ctx.channel().read();
            }
            else
            {
                ((ChannelFuture) future).channel().close();
            }
        });
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
    {
        cause.printStackTrace();
        HexDumpProxyFrontendHandler.closeOnFlush(ctx.channel());
    }
}
