// Created: 25.09.2019
package de.freese.demo.netty.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Thomas Freese
 */
public class HexDumpProxyBackendHandler extends ChannelInboundHandlerAdapter {
    private final Channel inboundChannel;

    public HexDumpProxyBackendHandler(final Channel inboundChannel) {
        super();

        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        HexDumpProxyFrontendHandler.closeOnFlush(this.inboundChannel);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        // ChannelFutureListener
        this.inboundChannel.writeAndFlush(msg).addListener(future -> {
            if (future.isSuccess()) {
                ctx.channel().read();
            }
            else {
                ((ChannelFuture) future).channel().close();
            }
        });
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        HexDumpProxyFrontendHandler.closeOnFlush(ctx.channel());
    }
}
