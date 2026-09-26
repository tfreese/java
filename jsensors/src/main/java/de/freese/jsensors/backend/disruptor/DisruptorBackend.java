package de.freese.jsensors.backend.disruptor;

import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.RoutingBackend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.JSensorThreadFactory;

/**
 * Use this with {@link RoutingBackend} to support multiple {@link Sensor}s.
 *
 * @author Thomas Freese
 * @since 27.10.2020
 */
public class DisruptorBackend implements Backend, AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorBackend.class);

    public static class Builder {
        private Backend delegate;

        /**
         * Default: Runtime.getRuntime().availableProcessors()
         */
        private int parallelism;

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
        private int ringBufferSize;

        Builder() {
            super();
        }

        public DisruptorBackend build() {
            if (parallelism < 1) {
                throw new IllegalArgumentException("parallelism < 1: " + parallelism);
            }

            if (ringBufferSize < 1) {
                throw new IllegalArgumentException("ringBufferSize < 1: " + ringBufferSize);
            }

            if (Integer.bitCount(ringBufferSize) != 1) {
                throw new IllegalArgumentException("ringBufferSize must be a power of 2");
            }

            final Disruptor<SensorEvent> disruptor = new Disruptor<>(SensorEvent::new, ringBufferSize, new JSensorThreadFactory("jSensor-disruptor-%d"));

            // EventHandler handles all the same Event -> LoadBalancing required if only one EventHandler should handle the Event.
            final EventHandler<SensorEvent>[] handlers = new DisruptorSensorHandler[parallelism];

            for (int i = 0; i < handlers.length; i++) {
                handlers[i] = new DisruptorSensorHandler(delegate, parallelism, i);
            }

            disruptor.handleEventsWith(handlers); //.then(new CleaningEventHandler());

            disruptor.start();

            return new DisruptorBackend(disruptor);
        }

        public Builder delegate(final Backend delegate) {
            this.delegate = delegate;

            return this;
        }

        public Builder parallelism(final int parallelism) {
            this.parallelism = parallelism;

            return this;
        }

        public Builder ringBufferSize(final int ringBufferSize) {
            this.ringBufferSize = ringBufferSize;

            return this;
        }

        public Builder withDefaultRingBufferSize() {
            return ringBufferSize(Integer.highestOneBit(parallelism) << 4);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Disruptor<SensorEvent> disruptor;

    DisruptorBackend(final Disruptor<SensorEvent> disruptor) {
        super();

        this.disruptor = disruptor;
    }

    @Override
    public void close() {
        // Only required if the Event-Publication is not finished.
        // disruptor.halt();

        try {
            disruptor.shutdown(3L, TimeUnit.SECONDS);
        }
        catch (final TimeoutException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void store(final SensorValue sensorValue) {
        if (sensorValue == null) {
            LOGGER.warn("sensorValue is null");
            return;
        }

        if (sensorValue.value() == null || sensorValue.value().isEmpty()) {
            LOGGER.warn("sensorValue without content");
            return;
        }

        final RingBuffer<SensorEvent> ringBuffer = disruptor.getRingBuffer();

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
