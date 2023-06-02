// Created: 02.09.2021
package de.freese.jsensors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import de.freese.jsensors.backend.MapBackend;
import de.freese.jsensors.backend.NoOpBackend;
import de.freese.jsensors.registry.DefaultSensorRegistry;
import de.freese.jsensors.registry.ScheduledSensorRegistry;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.JSensorThreadFactory;
import de.freese.jsensors.utils.SyncFuture;

/**
 * @author Thomas Freese
 */
class TestRegistries {
    @Test
    void testDefaultSensorRegistry() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();

        MapBackend mapBackend = new MapBackend(3);
        Sensor.builder("test", "obj", Function.identity()).register(registry, mapBackend);

        Exception exception = assertThrows(IllegalStateException.class, () -> registry.registerSensor("test", "", Function.identity(), "", NoOpBackend.getInstance()));
        assertEquals("sensor already exist: 'test'", exception.getMessage());

        Sensor sensor = registry.getSensor("test");
        assertNotNull(sensor);

        registry.measureAll();

        assertEquals(1, mapBackend.size("test"));

        SensorValue sensorValue = mapBackend.getLastValue("test");

        assertNotNull(sensorValue);
        assertEquals("obj", sensorValue.getValue());
    }

    @Test
    void testScheduledSensorRegistry() throws Exception {
        ScheduledSensorRegistry registry = new ScheduledSensorRegistry(new JSensorThreadFactory("test"), 2);

        SyncFuture<SensorValue> syncFuture = new SyncFuture<>();
        Sensor.builder("test", "obj", Function.identity()).register(registry, syncFuture::setResponse);

        Exception exception = assertThrows(IllegalStateException.class, () -> registry.registerSensor("test", "", Function.identity(), "", NoOpBackend.getInstance()));
        assertEquals("sensor already exist: 'test'", exception.getMessage());

        exception = assertThrows(IllegalStateException.class, () -> registry.scheduleSensor("test", 0, 1, TimeUnit.SECONDS));
        assertEquals("scheduler is not started: call #start() first", exception.getMessage());

        registry.start();

        Sensor sensor = registry.getSensor("test");
        assertNotNull(sensor);

        registry.scheduleSensor("test", 0, 1, TimeUnit.SECONDS);

        SensorValue sensorValue = syncFuture.get();
        registry.stop();

        assertNotNull(sensorValue);
        assertEquals("obj", sensorValue.getValue());
    }
}
