// Created: 08.09.2020
package de.freese.sonstiges.server.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.server.handler.IoHandler;

/**
 * @author Thomas Freese
 */
class HttpReadHandler implements CompletionHandler<Integer, MyAttachment> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpReadHandler.class);

    @Override
    public void completed(final Integer bytesRead, final MyAttachment attachment) {
        final AsynchronousSocketChannel channel = attachment.channel();
        final ByteBuffer byteBuffer = attachment.byteBuffer();
        final StringBuilder httpHeader = attachment.httpHeader();

        try {
            LOGGER.debug("{}: Read Request", channel.getRemoteAddress());
        }
        catch (final IOException ex) {
            failed(ex, null);
        }

        if (bytesRead <= 0) {
            // Nothing to read, Request complete.
            // Transfer WRITE-Operation another Thread.
            write(channel);
            return;
        }

        final Charset charset = IoHandler.DEFAULT_CHARSET;

        byteBuffer.flip();
        final CharBuffer charBuffer = charset.decode(byteBuffer);

        final String request = charBuffer.toString();
        LOGGER.debug("{}", request);

        httpHeader.append(request);

        byteBuffer.clear();

        final int length = httpHeader.length();

        final char[] endOfHeader = new char[4];
        httpHeader.getChars(length - 4, length, endOfHeader, 0);

        if (endOfHeader[0] == '\r' && endOfHeader[1] == '\n' && endOfHeader[2] == '\r' && endOfHeader[3] == '\n') {
            // Empty Line = End of the HttpHeader.
            write(channel);
        } else {
            // Next READ-Operation in this Thread.
            channel.read(byteBuffer, attachment, this);

            // Next READ-Operation in another Thread.
            // read(channel, byteBuffer);
        }
    }

    @Override
    public void failed(final Throwable exc, final MyAttachment attachment) {
        if (attachment != null) {
            final AsynchronousSocketChannel channel = attachment.channel();

            ServerAsync.close(channel, LOGGER);
        }

        LOGGER.error(exc.getMessage(), exc);
    }

    private void write(final AsynchronousSocketChannel channel) {
        final Charset charset = IoHandler.DEFAULT_CHARSET;

        final CharBuffer charBufferBody = CharBuffer.allocate(256)
                .put("<html>").put(System.lineSeparator())
                .put("<head>").put(System.lineSeparator())
                .put("<title>NIO Test</title>").put(System.lineSeparator())
                .put("<meta charset=\"UTF-8\">").put(System.lineSeparator())
                .put("</head>").put(System.lineSeparator())
                .put("<body>").put(System.lineSeparator())
                .put("Date: " + LocalDateTime.now(ZoneId.systemDefault()) + "<br>").put(System.lineSeparator())
                .put("</body>").put(System.lineSeparator())
                .put("</html>").put(System.lineSeparator());

        final CharBuffer charBuffer = CharBuffer.allocate(1024)
                .put("HTTP/1.1 200 OK").put(System.lineSeparator())
                .put("Server: nio").put(System.lineSeparator())
                .put("Content-type: text/html").put(System.lineSeparator())
                .put("Content-length: " + (charBufferBody.position() * 2)).put(System.lineSeparator())
                .put(System.lineSeparator());

        charBufferBody.flip();
        charBuffer.put(charBufferBody);
        charBuffer.flip();

        final ByteBuffer byteBuffer = charset.encode(charBuffer);

        final MyAttachment attachment = new MyAttachment(byteBuffer, channel);

        channel.write(byteBuffer, attachment, new HttpWriteHandler());
    }
}
