// Created: 04.11.2018
package de.freese.sonstiges.server.handler;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.time.LocalDateTime;

import de.freese.sonstiges.server.ServerMain;

/**
 * @author Thomas Freese
 */
public class HttpIoHandler extends AbstractIoHandler<SelectionKey> {
    @Override
    public void read(final SelectionKey selectionKey) {
        try {
            getLogger().debug("{}: read request", ServerMain.getRemoteAddress(selectionKey));

            final CharsetDecoder charsetDecoder = getCharsetDecoder();

            final ReadableByteChannel channel = (ReadableByteChannel) selectionKey.channel();

            final ByteBuffer inputBuffer = ByteBuffer.allocate(1024);

            while (channel.read(inputBuffer) > 0) {
                inputBuffer.flip();

                final CharBuffer charBuffer = charsetDecoder.decode(inputBuffer);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("{}", charBuffer.toString().strip());
                }

                inputBuffer.clear();
            }

            // WRITE-Mode for Channel.
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    @Override
    public void write(final SelectionKey selectionKey) {
        try {
            getLogger().debug("{}: write response", ServerMain.getRemoteAddress(selectionKey));

            final CharsetEncoder charsetEncoder = getCharsetEncoder();

            final WritableByteChannel channel = (WritableByteChannel) selectionKey.channel();

            final CharBuffer charBufferBody = CharBuffer.allocate(256);
            charBufferBody.put("<html>").put(System.lineSeparator());
            charBufferBody.put("<head>").put(System.lineSeparator());
            charBufferBody.put("<title>NIO Test</title>").put(System.lineSeparator());
            charBufferBody.put("<meta charset=\"UTF-8\">").put(System.lineSeparator());
            charBufferBody.put("</head>").put(System.lineSeparator());
            charBufferBody.put("<body>").put(System.lineSeparator());
            charBufferBody.put("Date: " + LocalDateTime.now() + "<br>").put(System.lineSeparator());
            charBufferBody.put("</body>").put(System.lineSeparator());
            charBufferBody.put("</html>").put(System.lineSeparator());

            final CharBuffer charBuffer = CharBuffer.allocate(1024);
            charBuffer.put("HTTP/1.1 200 OK").put(System.lineSeparator());
            charBuffer.put("Server: nio").put(System.lineSeparator());
            charBuffer.put("Content-type: text/html").put(System.lineSeparator());
            charBuffer.put("Content-length: " + (charBufferBody.position() * 2)).put(System.lineSeparator());
            charBuffer.put(System.lineSeparator());

            charBufferBody.flip();
            charBuffer.put(charBufferBody);
            charBuffer.flip();

            final ByteBuffer buffer = charsetEncoder.encode(charBuffer);
            // int bytesWritten = 0;

            while (buffer.hasRemaining()) {
                // bytesWritten +=
                channel.write(buffer);
            }

            // For HTTP, the session is over after the Response.
            channel.close();
            selectionKey.cancel();

            // Otherwise again READ-Mode.
            // selectionKey.interestOps(SelectionKey.OP_READ);
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
