// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.nio.ByteBuffer;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * @author Thomas Freese
 */
public class LongEventProducerWithTranslator
{
    /**
     *
     */
    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR = (event, sequence, bb) -> event.setValue(bb.getLong(0));
    /**
     *
     */
    private final RingBuffer<LongEvent> ringBuffer;

    /**
     * @param ringBuffer {@link com.lmax.disruptor.RingBuffer}
     */
    public LongEventProducerWithTranslator(final RingBuffer<LongEvent> ringBuffer)
    {
        super();

        this.ringBuffer = ringBuffer;
    }

    /**
     * @param bb {@link java.nio.ByteBuffer}
     */
    public void onData(final ByteBuffer bb)
    {
        this.ringBuffer.publishEvent(TRANSLATOR, bb);
    }
}
