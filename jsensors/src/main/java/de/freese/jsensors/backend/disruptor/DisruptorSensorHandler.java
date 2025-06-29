// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.Objects;

import com.lmax.disruptor.EventHandler;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
class DisruptorSensorHandler implements EventHandler<SensorEvent> {
    private final Backend backend;
    private final int ordinal;
    private final int parallelism;

    DisruptorSensorHandler(final Backend backend) {
        this(backend, 0, -1);
    }

    DisruptorSensorHandler(final Backend backend, final int parallelism, final int ordinal) {
        super();

        this.backend = Objects.requireNonNull(backend, "backend required");
        this.parallelism = parallelism;
        this.ordinal = ordinal;
    }

    @Override
    public void onEvent(final SensorEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        // Load-Balancing for the Handler by Sequence, otherwise all Handler would handle the same Sequence.
        if (ordinal == -1 || ordinal == (sequence % parallelism)) {
            final SensorValue sensorValue = event.getSensorValue();
            event.setSensorValue(null);

            store(sensorValue);
        }
    }

    private void store(final SensorValue sensorValue) {
        backend.store(sensorValue);
    }
}
