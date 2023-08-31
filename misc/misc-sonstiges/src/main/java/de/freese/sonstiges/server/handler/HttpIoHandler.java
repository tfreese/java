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
 * @see IoHandler
 */
public class HttpIoHandler extends AbstractIoHandler<SelectionKey> {
    @Override
    public void read(final SelectionKey selectionKey) {
        try {
            getLogger().debug("{}: read request", ServerMain.getRemoteAddress(selectionKey));

            CharsetDecoder charsetDecoder = getCharsetDecoder();

            ReadableByteChannel channel = (ReadableByteChannel) selectionKey.channel();

            ByteBuffer inputBuffer = ByteBuffer.allocate(1024);

            while (channel.read(inputBuffer) > 0) {
                inputBuffer.flip();

                CharBuffer charBuffer = charsetDecoder.decode(inputBuffer);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("\n{}", charBuffer.toString().strip());
                }

                inputBuffer.clear();
            }

            // WRITE-Mode für Channel.
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

            CharsetEncoder charsetEncoder = getCharsetEncoder();

            WritableByteChannel channel = (WritableByteChannel) selectionKey.channel();

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

            ByteBuffer buffer = charsetEncoder.encode(charBuffer);
            // int bytesWritten = 0;

            while (buffer.hasRemaining()) {
                // bytesWritten +=
                channel.write(buffer);
            }

            // For HTTP the session is over after the Response.
            channel.close();
            selectionKey.cancel();

            // Otherwise again READ-Mode:
            // selectionKey.interestOps(SelectionKey.OP_READ);
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
