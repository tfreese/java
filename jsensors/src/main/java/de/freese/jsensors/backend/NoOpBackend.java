// Created: 01.06.23
package de.freese.jsensors.backend;

import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
public final class NoOpBackend implements Backend {

    private static final class NoOpBackendHolder {
        private static final NoOpBackend INSTANCE = new NoOpBackend();

        private NoOpBackendHolder() {
            super();
        }
    }

    public static NoOpBackend getInstance() {
        return NoOpBackendHolder.INSTANCE;
    }

    private NoOpBackend() {
        super();
    }

    @Override
    public void store(final SensorValue sensorValue) {
        // Empty
    }
}
