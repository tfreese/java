// Created: 08.09.2020
package de.freese.sonstiges.server.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.server.handler.IoHandler;

/**
 * @author Thomas Freese
 */
class HttpReadHandler implements CompletionHandler<Integer, MyAttachment> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpReadHandler.class);

    /**
     * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
     */
    @Override
    public void completed(final Integer bytesRead, final MyAttachment attachment) {
        AsynchronousSocketChannel channel = attachment.channel();
        ByteBuffer byteBuffer = attachment.byteBuffer();
        StringBuilder httpHeader = attachment.httpHeader();

        try {
            LOGGER.debug("{}: Read Request", channel.getRemoteAddress());
        }
        catch (IOException ex) {
            failed(ex, null);
        }

        if (bytesRead <= 0) {
            // Nichts mehr zum Lesen, Request vollständig.
            // Write Vorgang an anderen Thread übergeben.
            write(channel);
            return;
        }

        Charset charset = IoHandler.DEFAULT_CHARSET;

        byteBuffer.flip();
        CharBuffer charBuffer = charset.decode(byteBuffer);

        String request = charBuffer.toString();
        LOGGER.debug("\n{}", request);

        httpHeader.append(request);

        byteBuffer.clear();

        int length = httpHeader.length();

        char[] endOfHeader = new char[4];
        httpHeader.getChars(length - 4, length, endOfHeader, 0);

        if ((endOfHeader[0] == '\r') && (endOfHeader[1] == '\n') && (endOfHeader[2] == '\r') && (endOfHeader[3] == '\n')) {
            // Leerzeile = Ende des HttpHeaders.
            write(channel);
        }
        else {
            // Nächster Lese Vorgang in diesem Thread,
            channel.read(byteBuffer, attachment, this);

            // Nächster Lese Vorgang im anderen Thread.
            // read(channel, byteBuffer);
        }
    }

    /**
     * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
     */
    @Override
    public void failed(final Throwable exc, final MyAttachment attachment) {
        AsynchronousSocketChannel channel = attachment.channel();

        ServerAsync.close(channel, LOGGER);
        LOGGER.error(exc.getMessage(), exc);
    }

    private void write(final AsynchronousSocketChannel channel) {
        Charset charset = IoHandler.DEFAULT_CHARSET;

        CharBuffer charBufferBody = CharBuffer.allocate(256);
        charBufferBody.put("<html>").put("\r\n");
        charBufferBody.put("<head>").put("\r\n");
        charBufferBody.put("<title>NIO Test</title>").put("\r\n");
        charBufferBody.put("<meta charset=\"UTF-8\">").put("\r\n");
        charBufferBody.put("</head>").put("\r\n");
        charBufferBody.put("<body>").put("\r\n");
        charBufferBody.put("Date: " + LocalDateTime.now() + "<br>").put("\r\n");
        charBufferBody.put("</body>").put("\r\n");
        charBufferBody.put("</html>").put("\r\n");

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("HTTP/1.1 200 OK").put("\r\n");
        charBuffer.put("Server: nio").put("\r\n");
        charBuffer.put("Content-type: text/html").put("\r\n");
        charBuffer.put("Content-length: " + (charBufferBody.position() * 2)).put("\r\n");
        charBuffer.put("\r\n");

        charBufferBody.flip();
        charBuffer.put(charBufferBody);
        charBuffer.flip();

        ByteBuffer byteBuffer = charset.encode(charBuffer);

        MyAttachment attachment = new MyAttachment(byteBuffer, channel);

        channel.write(byteBuffer, attachment, new HttpWriteHandler());
    }
}
