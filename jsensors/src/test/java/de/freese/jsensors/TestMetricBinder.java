package de.freese.jsensors;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;

import de.freese.jsensors.binder.CpuMetrics;
import de.freese.jsensors.binder.DiscMetrics;
import de.freese.jsensors.binder.ExecutorServiceMetrics;
import de.freese.jsensors.binder.MemoryMetrics;
import de.freese.jsensors.binder.SwapMetrics;
import de.freese.jsensors.binder.ThreadMetrics;
import de.freese.jsensors.registry.Sensors;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 * @since 02.09.2021
 */
@SuppressWarnings("java:S5778")
class TestMetricBinder {
    @Test
    void testCpuMetrics() {
        new CpuMetrics().bindTo(Sensors.GLOBAL_REGISTRY);

        Sensors.getSensors().forEach(Sensor::nextValue);

        await().pollDelay(Duration.ofMillis(300L)).until(() -> true);

        final SensorValue sensorValue = Sensors.nextValue("cpu.usage");
        assertNotNull(sensorValue);
        assertTrue(sensorValue.getValueAsDouble() > 0D);

        await().pollDelay(Duration.ofMillis(300L)).until(() -> true);

        Sensors.getSensors().forEach(Sensor::nextValue);

        final SensorValue sensorValue1 = Sensors.nextValue("cpu.usage");
        assertNotNull(sensorValue1);
        assertNotEquals(sensorValue1.timestamp(), sensorValue.timestamp());
        assertTrue(sensorValue.getValueAsDouble() > 0D);
    }

    @Test
    void testDiscMetrics() {
        new DiscMetrics("tmp1", Path.of(System.getProperty("java.io.tmpdir"))).bindTo(Sensors.GLOBAL_REGISTRY);

        Sensors.getSensors().forEach(Sensor::nextValue);

        final SensorValue sensorValuePathFree = Sensors.nextValue("disk.free.tmp1");
        final SensorValue sensorValuePathUsage = Sensors.nextValue("disk.usage.tmp1");
        assertNotNull(sensorValuePathFree);
        assertNotNull(sensorValuePathUsage);

        new DiscMetrics("tmp2", new File(System.getProperty("java.io.tmpdir"))).bindTo(Sensors.GLOBAL_REGISTRY);

        Sensors.getSensors().forEach(Sensor::nextValue);

        final SensorValue sensorValueFileFree = Sensors.nextValue("disk.free.tmp2");
        final SensorValue sensorValueFileUsage = Sensors.nextValue("disk.usage.tmp2");
        assertNotNull(sensorValueFileFree);
        assertNotNull(sensorValueFileUsage);

        // Max. Difference: 4kb
        final long delta = 1024L * 4L;
        assertEquals(sensorValuePathFree.getValueAsLong(), sensorValueFileFree.getValueAsLong(), delta, "'free' sensor values not equal");
        assertEquals(sensorValuePathUsage.getValueAsDouble(), sensorValueFileUsage.getValueAsDouble(), delta, "'usage' sensor values not equal");
    }

    @Test
    void testExecutorServiceMetrics() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new ExecutorServiceMetrics(Executors.newSingleThreadExecutor(), "myExecutor").bindTo(Sensors.GLOBAL_REGISTRY));
        String expectedMessage = "executorService not supported: 'java.util.concurrent.Executors$AutoShutdownDelegatedExecutorService'";
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());

        //        new ExecutorServiceMetrics(Executors.newSingleThreadScheduledExecutor(), "myScheduler").bindTo(registry, name -> mapBackend);
        exception = assertThrows(IllegalArgumentException.class,
                () -> new ExecutorServiceMetrics(Executors.newSingleThreadScheduledExecutor(), "myScheduler").bindTo(Sensors.GLOBAL_REGISTRY));
        expectedMessage = "executorService not supported: 'java.util.concurrent.Executors$DelegatedScheduledExecutorService'";
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());

        new ExecutorServiceMetrics(ForkJoinPool.commonPool(), "myForkJoin").bindTo(Sensors.GLOBAL_REGISTRY);
        new ExecutorServiceMetrics(Executors.newFixedThreadPool(1), "myExecutor2").bindTo(Sensors.GLOBAL_REGISTRY);
        new ExecutorServiceMetrics(Executors.newScheduledThreadPool(1), "myScheduler2").bindTo(Sensors.GLOBAL_REGISTRY);

        Sensors.getSensors().forEach(Sensor::nextValue);

        final Sensor sensorForkJoin = Sensors.getSensor("executor.active.myForkJoin");
        final Sensor sensorExecutor = Sensors.getSensor("executor.active.myExecutor2");
        final Sensor sensorScheduler = Sensors.getSensor("executor.active.myScheduler2");

        assertNotNull(sensorForkJoin);
        assertNotNull(sensorExecutor);
        assertNotNull(sensorScheduler);

        final SensorValue sensorValueForkJoin = sensorForkJoin.nextValue();
        final SensorValue sensorValueExecutor = sensorExecutor.nextValue();
        final SensorValue sensorValueScheduler = sensorScheduler.nextValue();

        assertNotNull(sensorValueForkJoin);
        assertNotNull(sensorValueExecutor);
        assertNotNull(sensorValueScheduler);
    }

    @Test
    void testMemoryMetrics() {
        new MemoryMetrics().bindTo(Sensors.GLOBAL_REGISTRY);

        Sensors.getSensors().forEach(Sensor::nextValue);

        final SensorValue sensorValueFree = Sensors.nextValue("memory.free");
        final SensorValue sensorValueMax = Sensors.nextValue("memory.max");
        final SensorValue sensorValueUsage = Sensors.nextValue("memory.usage");

        assertNotNull(sensorValueFree);
        assertNotNull(sensorValueMax);
        assertNotNull(sensorValueUsage);

        assertTrue(sensorValueFree.getValueAsLong() > 0L);
        assertTrue(sensorValueMax.getValueAsLong() > 0L);
        assertTrue(sensorValueUsage.getValueAsDouble() > 0D);

        // System.out.printf("memory.free: %.3f MB%n", sensorValueFree.getValueAsLong() / 1024D / 1024D);
        // System.out.printf("memory.max: %.3f MB%n", sensorValueMax.getValueAsLong() / 1024D / 1024D);
        // System.out.printf("memory.usage: %.3f %%%n", sensorValueUsage.getValueAsDouble());
    }

    @Test
    void testSwapMetrics() {
        new SwapMetrics().bindTo(Sensors.GLOBAL_REGISTRY);

        Sensors.getSensors().forEach(Sensor::nextValue);

        final SensorValue sensorValueFree = Sensors.nextValue("swap.free");
        final SensorValue sensorValueTotal = Sensors.nextValue("swap.total");
        final SensorValue sensorValueUsage = Sensors.nextValue("swap.usage");

        assertNotNull(sensorValueFree);
        assertNotNull(sensorValueTotal);
        assertNotNull(sensorValueUsage);

        assertTrue(sensorValueFree.getValueAsDouble() > 0D);
        assertTrue(sensorValueTotal.getValueAsDouble() > 0D);
        assertTrue(sensorValueUsage.getValueAsDouble() > 0D);
    }

    @Test
    void testThreadMetrics() {
        new ThreadMetrics().bindTo(Sensors.GLOBAL_REGISTRY);

        Sensors.getSensors().forEach(Sensor::nextValue);

        final SensorValue sensorValueCount = Sensors.nextValue("thread.count");

        assertNotNull(sensorValueCount);

        assertTrue(sensorValueCount.getValueAsLong() > 0L);
    }
}
