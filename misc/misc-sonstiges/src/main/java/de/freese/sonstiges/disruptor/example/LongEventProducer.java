// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.nio.ByteBuffer;
import com.lmax.disruptor.RingBuffer;

/**
 * @author Thomas Freese
 */
public class LongEventProducer
{
    /**
     *
     */
    private final RingBuffer<LongEvent> ringBuffer;

    /**
     * @param ringBuffer {@link com.lmax.disruptor.RingBuffer}
     */
    public LongEventProducer(final RingBuffer<LongEvent> ringBuffer)
    {
        super();

        this.ringBuffer = ringBuffer;
    }

    /**
     * @param bb {@link java.nio.ByteBuffer}
     */
    public void onData(final ByteBuffer bb)
    {
        // Grab the next sequence
        long sequence = this.ringBuffer.next();

        try
        {
            // Get the entry in the Disruptor
            LongEvent event = this.ringBuffer.get(sequence);

            // for the sequence
            event.setValue(bb.getLong(0));  // Fill with data
        }
        finally
        {
            this.ringBuffer.publish(sequence);
        }
    }
}
