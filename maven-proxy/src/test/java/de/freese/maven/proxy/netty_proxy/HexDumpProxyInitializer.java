// Created: 25.09.2019
package de.freese.maven.proxy.netty_proxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Thomas Freese
 */
public class HexDumpProxyInitializer extends ChannelInitializer<SocketChannel>
{
    private final String remoteHost;

    private final int remotePort;

    public HexDumpProxyInitializer(final String remoteHost, final int remotePort)
    {
        super();

        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    /**
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    public void initChannel(final SocketChannel ch)
    {
        // @formatter:off
        ch.pipeline()
            .addLast(new LoggingHandler(LogLevel.INFO), new HexDumpProxyFrontendHandler(this.remoteHost, this.remotePort));
        // @formatter:on
    }
}
