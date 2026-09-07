package de.freese.sonstiges.server.async;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author Thomas Freese
 * @since 08.09.2020
 */
record MyAttachment(StringBuilder httpHeader, ByteBuffer byteBuffer, AsynchronousSocketChannel channel) {
    MyAttachment(final ByteBuffer byteBuffer, final AsynchronousSocketChannel channel) {
        this(new StringBuilder(), byteBuffer, channel);
    }
}
