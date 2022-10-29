// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.nio.ByteBuffer;

/**
 * @author Thomas Freese
 */
public class HttpEvent
{
    /**
     *
     */
    private ByteBuffer buffer;
    /**
     *
     */
    private int numRead;
    /**
     *
     */
    private String requestId;

    /**
     *
     */
    public void clear()
    {
        setBuffer(null);
        setNumRead(-1);
        setRequestId(null);
    }

    /**
     * @return {@link ByteBuffer}
     */
    public ByteBuffer getBuffer()
    {
        return this.buffer;
    }

    /**
     * @return int
     */
    public int getNumRead()
    {
        return this.numRead;
    }

    /**
     * @return String
     */
    public String getRequestId()
    {
        return this.requestId;
    }

    /**
     * @param buffer {@link ByteBuffer}
     */
    public void setBuffer(final ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    /**
     * @param numRead int
     */
    public void setNumRead(final int numRead)
    {
        this.numRead = numRead;
    }

    /**
     * @param requestId String
     */
    public void setRequestId(final String requestId)
    {
        this.requestId = requestId;
    }
}
