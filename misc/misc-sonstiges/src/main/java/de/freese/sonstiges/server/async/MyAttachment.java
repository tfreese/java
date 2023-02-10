// Created: 08.09.2020
package de.freese.sonstiges.server.async;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author Thomas Freese
 */
record MyAttachment(StringBuilder httpHeader, ByteBuffer byteBuffer, AsynchronousSocketChannel channel) {
    MyAttachment(final ByteBuffer byteBuffer, final AsynchronousSocketChannel channel) {
        this(new StringBuilder(), byteBuffer, channel);
    }
}
