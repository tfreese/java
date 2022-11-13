// Created: 08.09.2020
package de.freese.sonstiges.server.async;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author Thomas Freese
 */
class MyAttachment
{
    final StringBuilder httpHeader = new StringBuilder();

    ByteBuffer byteBuffer;

    AsynchronousSocketChannel channel;
}
