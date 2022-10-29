// Created: 02.09.2021
package de.freese.jsensors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import de.freese.jsensors.registry.DefaultSensorRegistry;
import de.freese.jsensors.registry.ScheduledSensorRegistry;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.JSensorThreadFactory;
import de.freese.jsensors.utils.SyncFuture;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestRegistries
{
    @Test
    void testDuplicateSensorName()
    {
        SensorRegistry registry = new DefaultSensorRegistry();
        Sensor.builder("test.1", "obj", Function.identity()).register(registry);

        Exception exception = assertThrows(IllegalStateException.class, () -> Sensor.builder("test.1", "", Function.identity()).register(registry));

        assertEquals("sensor already exist: 'test.1'", exception.getMessage());
    }

    @Test
    void testKeepLastNValues() throws Exception
    {
        Sensor sensor = Sensor.builder("test", "obj", Function.identity()).keepLastNValues(3).register(new DefaultSensorRegistry());

        sensor.measure();
        assertEquals(1, sensor.getValues().size());

        sensor.measure();
        assertEquals(2, sensor.getValues().size());

        sensor.measure();
        assertEquals(3, sensor.getValues().size());

        sensor.measure();
        assertEquals(3, sensor.getValues().size());
    }

    @Test
    void testScheduledSensorRegistry() throws Exception
    {
        ScheduledSensorRegistry registry = new ScheduledSensorRegistry(new JSensorThreadFactory("test"), 2);

        Sensor.builder("test", "obj", Function.identity()).register(registry);

        Exception exception = assertThrows(IllegalStateException.class, () -> registry.scheduleSensor("test", 0, 1, TimeUnit.SECONDS, Objects::isNull));
        assertEquals("scheduler is not started: call #start() first", exception.getMessage());

        registry.start();

        Sensor sensor = registry.getSensor("test");
        assertNotNull(sensor);
        assertEquals(0, sensor.getValues().size());

        SyncFuture<SensorValue> syncFuture = new SyncFuture<>();

        registry.scheduleSensor("test", 0, 1, TimeUnit.SECONDS, syncFuture::setResponse);

        SensorValue sensorValue = syncFuture.get();
        registry.stop();

        assertNotNull(sensorValue);
        assertEquals(1, sensor.getValues().size());
    }
}
