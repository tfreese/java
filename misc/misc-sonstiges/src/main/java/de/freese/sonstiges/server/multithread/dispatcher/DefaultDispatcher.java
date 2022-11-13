// Created: 08.09.2020
package de.freese.sonstiges.server.multithread.dispatcher;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import de.freese.sonstiges.server.handler.IoHandler;
import de.freese.sonstiges.server.multithread.AbstractNioProcessor;

/**
 * Der {@link Dispatcher} kümmert sich um das Connection-Handling der Clients nach dem 'accept'.<br>
 * Der {@link IoHandler} übernimmt das Lesen und Schreiben von Request und Response in einem separaten Thread.<br>
 *
 * @author Thomas Freese
 */
class DefaultDispatcher extends AbstractNioProcessor implements Dispatcher
{
    private final Executor executor;

    private final IoHandler<SelectionKey> ioHandler;

    private final Queue<SocketChannel> newSessions = new ConcurrentLinkedQueue<>();

    DefaultDispatcher(final Selector selector, final IoHandler<SelectionKey> ioHandler, final Executor executor)
    {
        super(selector);

        this.ioHandler = Objects.requireNonNull(ioHandler, "ioHandler required");
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    /**
     * @see de.freese.sonstiges.server.multithread.dispatcher.Dispatcher#register(java.nio.channels.SocketChannel)
     */
    @Override
    public void register(final SocketChannel socketChannel)
    {
        if (isShutdown())
        {
            return;
        }

        Objects.requireNonNull(socketChannel, "socketChannel required");

        try
        {
            getLogger().debug("{}: register new channel", socketChannel.getRemoteAddress());

            getNewSessions().add(socketChannel);

            getSelector().wakeup();
        }
        catch (Exception ex)
        {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#afterSelectorLoop()
     */
    @Override
    protected void afterSelectorLoop()
    {
        // Die neuen Channels zum Selector hinzufügen.
        processNewChannels();
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#afterSelectorWhile()
     */
    @Override
    protected void afterSelectorWhile()
    {
        // Neue Channels gleich wieder schliessen.
        for (Iterator<SocketChannel> iterator = getNewSessions().iterator(); iterator.hasNext(); )
        {
            SocketChannel socketChannel = iterator.next();
            iterator.remove();

            try
            {

                socketChannel.close();
            }
            catch (Exception ex)
            {
                getLogger().error(ex.getMessage(), ex);
            }
        }

        super.afterSelectorWhile();
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#onReadable(java.nio.channels.SelectionKey)
     */
    @Override
    protected void onReadable(final SelectionKey selectionKey)
    {
        // Request lesen.
        // this.ioHandler.read(selectionKey);

        selectionKey.interestOps(0); // Selector-Selektion deaktivieren.

        this.executor.execute(() ->
        {
            this.ioHandler.read(selectionKey);
            selectionKey.selector().wakeup();
        });
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#onWritable(java.nio.channels.SelectionKey)
     */
    @Override
    protected void onWritable(final SelectionKey selectionKey)
    {
        // Response schreiben.
        // this.ioHandler.write(selectionKey);

        selectionKey.interestOps(0); // Selector-Selektion deaktivieren.

        this.executor.execute(() ->
        {
            this.ioHandler.write(selectionKey);
            selectionKey.selector().wakeup();
        });
    }

    private Queue<SocketChannel> getNewSessions()
    {
        return this.newSessions;
    }

    /**
     * Die neuen Channels zum Selector hinzufügen.
     *
     * @see #register(SocketChannel)
     */
    private void processNewChannels()
    {
        if (isShutdown())
        {
            return;
        }

        // for (SocketChannel socketChannel = getNewSessions().poll(); socketChannel != null; socketChannel = this.newSessions.poll())
        while (!getNewSessions().isEmpty())
        {
            SocketChannel socketChannel = getNewSessions().poll();

            if (socketChannel == null)
            {
                continue;
            }

            try
            {
                socketChannel.configureBlocking(false);

                getLogger().debug("{}: register channel on selector", socketChannel.getRemoteAddress());

                SelectionKey selectionKey = socketChannel.register(getSelector(), SelectionKey.OP_READ);
                // selectionKey.attach(obj)
            }
            catch (Exception ex)
            {
                getLogger().error(ex.getMessage(), ex);
            }
        }
    }
}
