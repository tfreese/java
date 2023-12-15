// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.nio.ByteBuffer;
import java.util.Map;

import com.lmax.disruptor.RingBuffer;

/**
 * @author Thomas Freese
 */
public class HttpEventProducer {
    private final Map<String, Boolean> mapResponseReady;
    private final RingBuffer<HttpEvent> ringBuffer;

    public HttpEventProducer(final RingBuffer<HttpEvent> ringBuffer, final Map<String, Boolean> mapResponseReady) {
        super();

        this.ringBuffer = ringBuffer;
        this.mapResponseReady = mapResponseReady;
    }

    public void onData(final String requestId, final ByteBuffer buffer, final int numRead) {
        final long sequence = this.ringBuffer.next();

        try {
            final HttpEvent event = this.ringBuffer.get(sequence);

            event.setBuffer(buffer);
            event.setRequestId(requestId);
            event.setNumRead(numRead);
        }
        finally {
            this.mapResponseReady.put(requestId, Boolean.FALSE);
            this.ringBuffer.publish(sequence);
        }
    }
}
