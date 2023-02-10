// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.nio.ByteBuffer;

/**
 * @author Thomas Freese
 */
public class HttpEvent {
    private ByteBuffer buffer;

    private int numRead;

    private String requestId;

    public void clear() {
        setBuffer(null);
        setNumRead(-1);
        setRequestId(null);
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

    public int getNumRead() {
        return this.numRead;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setBuffer(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void setNumRead(final int numRead) {
        this.numRead = numRead;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }
}
