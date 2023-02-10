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

        new CpuMetrics().bindTo(registry);

        registry.measureAll();
        SensorValue sensorValue1 = registry.getSensor("cpu.usage").getValueLast();

        TimeUnit.MILLISECONDS.sleep(300);

        registry.measureAll();
        SensorValue sensorValue2 = registry.getSensor("cpu.usage").getValueLast();

        assertNotNull(sensorValue1);
        assertNotNull(sensorValue2);
        assertNotEquals(sensorValue1.getTimestamp(), sensorValue2.getTimestamp());
        assertEquals("", sensorValue1.getValue());
        assertTrue(sensorValue2.getValueAsDouble() > 0D);

        // System.out.println(sensorValue1);
        // System.out.println(sensorValue2);
    }

    @Test
    void testDiscMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();

        new DiscMetrics("tmp1", Path.of(System.getProperty("java.io.tmpdir"))).bindTo(registry);
        registry.measureAll();
        SensorValue sensorValuePathFree = registry.getSensor("disk.free.tmp1").getValueLast();
        SensorValue sensorValuePathUsage = registry.getSensor("disk.usage.tmp1").getValueLast();
        assertNotNull(sensorValuePathFree);
        assertNotNull(sensorValuePathUsage);

        new DiscMetrics("tmp2", new File(System.getProperty("java.io.tmpdir"))).bindTo(registry);
        registry.measureAll();
        SensorValue sensorValueFileFree = registry.getSensor("disk.free.tmp2").getValueLast();
        SensorValue sensorValueFileUsage = registry.getSensor("disk.usage.tmp2").getValueLast();
        assertNotNull(sensorValueFileFree);
        assertNotNull(sensorValueFileUsage);

        // Max. Difference: 4kb
        long delta = 1024 * 4;
        assertEquals(sensorValuePathFree.getValueAsLong(), sensorValueFileFree.getValueAsLong(), delta, "'free' sensor values not equal");
        assertEquals(sensorValuePathUsage.getValueAsDouble(), sensorValueFileUsage.getValueAsDouble(), delta, "'usage' sensor values not equal");
    }

    @Test
    void testExecutorServiceMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ExecutorServiceMetrics(Executors.newSingleThreadExecutor(), "myExecutor").bindTo(registry));
        String expectedMessage = "executorService not supported: 'java.util.concurrent.Executors$FinalizableDelegatedExecutorService'";
        assertEquals(exception.getMessage(), expectedMessage);

        exception = assertThrows(IllegalArgumentException.class, () -> new ExecutorServiceMetrics(Executors.newSingleThreadScheduledExecutor(), "myScheduler").bindTo(registry));
        expectedMessage = "executorService not supported: 'java.util.concurrent.Executors$DelegatedScheduledExecutorService'";
        assertEquals(exception.getMessage(), expectedMessage);

        new ExecutorServiceMetrics(ForkJoinPool.commonPool(), "myForkJoin").bindTo(registry);
        new ExecutorServiceMetrics(Executors.newFixedThreadPool(1), "myExecutor").bindTo(registry);
        new ExecutorServiceMetrics(Executors.newScheduledThreadPool(1), "myScheduler").bindTo(registry);

        registry.measureAll();

        Sensor sensorForkJoin = registry.getSensor("executor.active.myForkJoin");
        Sensor sensorExecutor = registry.getSensor("executor.active.myExecutor");
        Sensor sensorScheduler = registry.getSensor("executor.active.myScheduler");

        assertNotNull(sensorForkJoin);
        assertNotNull(sensorExecutor);
        assertNotNull(sensorScheduler);

        SensorValue sensorValueForkJoin = sensorForkJoin.getValueLast();
        SensorValue sensorValueExecutor = sensorExecutor.getValueLast();
        SensorValue sensorValueScheduler = sensorScheduler.getValueLast();

        assertNotNull(sensorValueForkJoin);
        assertNotNull(sensorValueExecutor);
        assertNotNull(sensorValueScheduler);
    }

    @Test
    void testMemoryMetrics() throws Exception {
        DefaultSensorRegistry registry = new DefaultSensorRegistry();

        new MemoryMetrics().bindTo(registry);

        registry.measureAll();

        SensorValue sensorValueFree = registry.getSensor("memory.free").getValueLast();
        SensorValue sensorValueMax = registry.getSensor("memory.max").getValueLast();
        SensorValue sensorValueTotal = registry.getSensor("memory.total").getValueLast();
        SensorValue sensorValueUsage = registry.getSensor("memory.usage").getValueLast();

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

        new SwapMetrics().bindTo(registry);

        registry.measureAll();

        SensorValue sensorValueFree = registry.getSensor("swap.free").getValueLast();
        SensorValue sensorValueUsage = registry.getSensor("swap.usage").getValueLast();

        assertNotNull(sensorValueFree);
        assertNotNull(sensorValueUsage);

        assertTrue(sensorValueFree.getValueAsLong() > 0D);
    }
}
