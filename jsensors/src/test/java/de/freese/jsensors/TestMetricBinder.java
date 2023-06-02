// Created: 02.09.2021
package de.freese.jsensors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import de.freese.jsensors.backend.MapBackend;
import de.freese.jsensors.binder.CpuMetrics;
import de.freese.jsensors.binder.DiscMetrics;
import de.freese.jsensors.binder.ExecutorServiceMetrics;
import de.freese.jsensors.binder.MemoryMetrics;
import de.freese.jsensors.binder.SwapMetrics;
import de.freese.jsensors.registry.DefaultSensorRegistry;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 */
class TestMetricBinder {
    @Test
    void testCpuMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();
        MapBackend mapBackend = new MapBackend(3);

        new CpuMetrics().bindTo(registry, name -> mapBackend);

        registry.measureAll();
        SensorValue sensorValue1 = mapBackend.getLastValue("cpu.usage");
        assertNotNull(sensorValue1);
        assertEquals("0", sensorValue1.getValue());

        TimeUnit.MILLISECONDS.sleep(300L);

        registry.measureAll();
        SensorValue sensorValue2 = mapBackend.getLastValue("cpu.usage");
        assertNotNull(sensorValue2);

        assertNotEquals(sensorValue1.getTimestamp(), sensorValue2.getTimestamp());

        assertTrue(sensorValue2.getValueAsDouble() > 0D);
    }

    @Test
    void testDiscMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();
        MapBackend mapBackend = new MapBackend(3);

        new DiscMetrics("tmp1", Path.of(System.getProperty("java.io.tmpdir"))).bindTo(registry, name -> mapBackend);

        registry.measureAll();
        SensorValue sensorValuePathFree = mapBackend.getLastValue("disk.free.tmp1");
        SensorValue sensorValuePathUsage = mapBackend.getLastValue("disk.usage.tmp1");
        assertNotNull(sensorValuePathFree);
        assertNotNull(sensorValuePathUsage);

        new DiscMetrics("tmp2", new File(System.getProperty("java.io.tmpdir"))).bindTo(registry, name -> mapBackend);

        registry.measureAll();
        SensorValue sensorValueFileFree = mapBackend.getLastValue("disk.free.tmp2");
        SensorValue sensorValueFileUsage = mapBackend.getLastValue("disk.usage.tmp2");
        assertNotNull(sensorValueFileFree);
        assertNotNull(sensorValueFileUsage);

        // Max. Difference: 4kb
        long delta = 1024L * 4;
        assertEquals(sensorValuePathFree.getValueAsLong(), sensorValueFileFree.getValueAsLong(), delta, "'free' sensor values not equal");
        assertEquals(sensorValuePathUsage.getValueAsDouble(), sensorValueFileUsage.getValueAsDouble(), delta, "'usage' sensor values not equal");
    }

    @Test
    void testExecutorServiceMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();
        MapBackend mapBackend = new MapBackend(3);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ExecutorServiceMetrics(Executors.newSingleThreadExecutor(), "myExecutor").bindTo(registry, name -> mapBackend));
        String expectedMessage = "executorService not supported: 'java.util.concurrent.Executors$FinalizableDelegatedExecutorService'";
        assertEquals(exception.getMessage(), expectedMessage);

        exception = assertThrows(IllegalArgumentException.class, () -> new ExecutorServiceMetrics(Executors.newSingleThreadScheduledExecutor(), "myScheduler").bindTo(registry, name -> mapBackend));
        expectedMessage = "executorService not supported: 'java.util.concurrent.Executors$DelegatedScheduledExecutorService'";
        assertEquals(exception.getMessage(), expectedMessage);

        new ExecutorServiceMetrics(ForkJoinPool.commonPool(), "myForkJoin").bindTo(registry, name -> mapBackend);
        new ExecutorServiceMetrics(Executors.newFixedThreadPool(1), "myExecutor").bindTo(registry, name -> mapBackend);
        new ExecutorServiceMetrics(Executors.newScheduledThreadPool(1), "myScheduler").bindTo(registry, name -> mapBackend);

        registry.measureAll();

        Sensor sensorForkJoin = registry.getSensor("executor.active.myForkJoin");
        Sensor sensorExecutor = registry.getSensor("executor.active.myExecutor");
        Sensor sensorScheduler = registry.getSensor("executor.active.myScheduler");

        assertNotNull(sensorForkJoin);
        assertNotNull(sensorExecutor);
        assertNotNull(sensorScheduler);

        SensorValue sensorValueForkJoin = mapBackend.getLastValue("executor.active.myForkJoin");
        SensorValue sensorValueExecutor = mapBackend.getLastValue("executor.active.myExecutor");
        SensorValue sensorValueScheduler = mapBackend.getLastValue("executor.active.myScheduler");

        assertNotNull(sensorValueForkJoin);
        assertNotNull(sensorValueExecutor);
        assertNotNull(sensorValueScheduler);
    }

    @Test
    void testMemoryMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();
        MapBackend mapBackend = new MapBackend(3);

        new MemoryMetrics().bindTo(registry, name -> mapBackend);

        registry.measureAll();

        SensorValue sensorValueFree = mapBackend.getLastValue("memory.free");
        SensorValue sensorValueMax = mapBackend.getLastValue("memory.max");
        SensorValue sensorValueTotal = mapBackend.getLastValue("memory.total");
        SensorValue sensorValueUsage = mapBackend.getLastValue("memory.usage");

        assertNotNull(sensorValueFree);
        assertNotNull(sensorValueMax);
        assertNotNull(sensorValueTotal);
        assertNotNull(sensorValueUsage);

        assertTrue(sensorValueFree.getValueAsLong() > 0L);
        assertTrue(sensorValueMax.getValueAsLong() > 0L);
        assertTrue(sensorValueTotal.getValueAsLong() > 0L);
        assertTrue(sensorValueUsage.getValueAsDouble() > 0D);

        // System.out.printf("memory.free: %.3f MB%n", sensorValueFree.getValueAsLong() / 1024D / 1024D);
        // System.out.printf("memory.max: %.3f MB%n", sensorValueMax.getValueAsLong() / 1024D / 1024D);
        // System.out.printf("memory.total: %.3f MB%n", sensorValueTotal.getValueAsLong( / 1024D / 1024D);
        // System.out.printf("memory.usage: %.3f %%%n", sensorValueUsage.getValueAsDouble());
    }

    @Test
    void testSwapMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();
        MapBackend mapBackend = new MapBackend(3);

        new SwapMetrics().bindTo(registry, name -> mapBackend);

        registry.measureAll();

        SensorValue sensorValueFree = mapBackend.getLastValue("swap.free");
        SensorValue sensorValueUsage = mapBackend.getLastValue("swap.usage");

        assertNotNull(sensorValueFree);
        assertNotNull(sensorValueUsage);

        assertTrue(sensorValueFree.getValueAsLong() > 0D);
    }
}
