// Created: 08.09.2020
package de.freese.sonstiges.server.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class HttpWriteHandler implements CompletionHandler<Integer, MyAttachment> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpWriteHandler.class);

    @Override
    public void completed(final Integer result, final MyAttachment attachment) {
        final AsynchronousSocketChannel channel = attachment.channel();
        final ByteBuffer byteBuffer = attachment.byteBuffer();

        try {
            LOGGER.debug("{}: Write Response", channel.getRemoteAddress());
        }
        catch (IOException ex) {
            failed(ex, null);
        }

        while (byteBuffer.hasRemaining()) {
            channel.write(byteBuffer, null, this);
        }

        ServerAsync.close(channel, LOGGER);
    }

    @Override
    public void failed(final Throwable exc, final MyAttachment attachment) {
        final AsynchronousSocketChannel channel = attachment.channel();

        ServerAsync.close(channel, LOGGER);
        LOGGER.error(exc.getMessage(), exc);
    }
}
