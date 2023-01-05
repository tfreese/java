// Created: 10.09.2020
package de.freese.sonstiges.server.multithread.dispatcher;

import java.nio.channels.SocketChannel;

/**
 * The {@link Dispatcher} handles the Client Connections after the 'accept'.<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Dispatcher
{
    void register(SocketChannel socketChannel);
}
