// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.RoutingBackend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.JSensorThreadFactory;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Use this with {@link RoutingBackend} to support multiple {@link Sensor}s.
 *
 * @author Thomas Freese
 */
public class DisruptorBackend extends AbstractBackend implements LifeCycle {
    private final Backend delegateBackend;

    /**
     * Default: Runtime.getRuntime().availableProcessors()
     */
    private final int parallelism;

    /**
     * Default: Integer.highestOneBit(Runtime.getRuntime().availableProcessors()) << 4)<br>
     * Example:<br>
     * 32 << 4 = 512<br>
     * 24 << 4 = 256<br>
     * 16 << 4 = 256<br>
     * 8 << 4 = 128<br>
     * 4 << 4 = 64<br>
     * 2 << 4 = 32<br>
     */
    private final int ringBufferSize;

    private Disruptor<SensorEvent> disruptor;

    public DisruptorBackend(final Backend delegateBackend, final int parallelism) {
        this(delegateBackend, parallelism, Integer.highestOneBit(parallelism) << 4);
    }

    /**
     * @param ringBufferSize int; must be a power of 2
     */
    public DisruptorBackend(final Backend delegateBackend, final int parallelism, final int ringBufferSize) {
        super();

        this.delegateBackend = Objects.requireNonNull(delegateBackend, "delegateBackend required");

        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        this.parallelism = parallelism;

        if (ringBufferSize < 1) {
            throw new IllegalArgumentException("ringBufferSize < 1: " + ringBufferSize);
        }

        if (Integer.bitCount(ringBufferSize) != 1) {
            throw new IllegalArgumentException("ringBufferSize must be a power of 2");
        }

        this.ringBufferSize = ringBufferSize;
    }

    @Override
    public void start() {
        this.disruptor = new Disruptor<>(SensorEvent::new, this.ringBufferSize, new JSensorThreadFactory("jSensor-disruptor-%d"));

        // EventHandler handles all the same Event -> LoadBalancing required if only one EventHandler should handle the Event.
        final EventHandler<SensorEvent>[] handlers = new DisruptorSensorHandler[this.parallelism];

        for (int i = 0; i < handlers.length; i++) {
            handlers[i] = new DisruptorSensorHandler(this.delegateBackend, this.parallelism, i);
        }

        this.disruptor.handleEventsWith(handlers); //.then(new CleaningEventHandler());

        this.disruptor.start();
    }

    @Override
    public void stop() {
        // Only required, if the Event-Publication is not finished.
        // this.disruptor.halt();

        try {
            this.disruptor.shutdown(3, TimeUnit.SECONDS);
        }
        catch (TimeoutException ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        final RingBuffer<SensorEvent> ringBuffer = this.disruptor.getRingBuffer();

        final long sequence = ringBuffer.next();

        try {
            final SensorEvent event = ringBuffer.get(sequence);

            event.setSensorValue(sensorValue);
        }
        finally {
            ringBuffer.publish(sequence);
        }
    }
}
