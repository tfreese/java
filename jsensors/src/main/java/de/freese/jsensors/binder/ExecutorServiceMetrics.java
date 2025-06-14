// Created: 03.09.2021
package de.freese.jsensors.binder;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;

/**
 * See io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics
 *
 * @author Thomas Freese
 */
public class ExecutorServiceMetrics implements SensorBinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceMetrics.class);

    private final ExecutorService executorService;
    private final String serviceName;

    public ExecutorServiceMetrics(final ExecutorService executorService, final String serviceName) {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.serviceName = Objects.requireNonNull(serviceName, "serviceName required");
    }

    @Override
    public List<String> bindTo(final SensorRegistry registry, final Function<String, Backend> backendProvider) {
        if (executorService instanceof ForkJoinPool fjp) {
            return bindTo(registry, fjp, backendProvider);
        }
        else if (executorService instanceof ThreadPoolExecutor tpe) {
            return bindTo(registry, tpe, backendProvider);
        }
        else {
            final String className = executorService.getClass().getName();
            ThreadPoolExecutor pool = null;

            if ("java.util.concurrent.Executors$DelegatedScheduledExecutorService".equals(className)) {
                pool = unwrapThreadPoolExecutor(executorService, executorService.getClass());
            }
            else if ("java.util.concurrent.Executors$AutoShutdownDelegatedExecutorService".equals(className)) {
                pool = unwrapThreadPoolExecutor(executorService, executorService.getClass().getSuperclass());
            }

            if (pool != null) {
                return bindTo(registry, pool, backendProvider);
            }
            else {
                // getLogger().warn("executorService not supported: {}", className);
                throw new IllegalArgumentException(String.format("executorService not supported: '%s'", className));
            }
        }
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    private List<String> bindTo(final SensorRegistry registry, final ForkJoinPool forkJoinPool, final Function<String, Backend> backendProvider) {
        Sensor.builder("executor.steals." + serviceName, forkJoinPool, pool -> Long.toString(pool.getStealCount())).description(
                        "Estimate of the total number of tasks stolen from one thread's work queue by another. The reported value "
                                + "underestimates the actual total number of steals when the pool is not quiescent")
                .register(registry, backendProvider);

        Sensor.builder("executor.queued." + serviceName, forkJoinPool, pool -> Long.toString(pool.getQueuedTaskCount()))
                .description("An estimate of the total number of tasks currently held in queues by worker threads").register(registry, backendProvider);

        Sensor.builder("executor.active." + serviceName, forkJoinPool, pool -> Integer.toString(pool.getActiveThreadCount()))
                .description("An estimate of the number of threads that are currently stealing or executing tasks").register(registry, backendProvider);

        Sensor.builder("executor.running." + serviceName, forkJoinPool, pool -> Integer.toString(pool.getRunningThreadCount()))
                .description("An estimate of the number of worker threads that are not blocked waiting to join tasks or for other managed synchronization threads")
                .register(registry, backendProvider);

        return List.of("executor.steals." + serviceName, "executor.queued." + serviceName, "executor.active." + serviceName, "executor.running." + serviceName);
    }

    private List<String> bindTo(final SensorRegistry registry, final ThreadPoolExecutor threadPoolExecutor, final Function<String, Backend> backendProvider) {
        Sensor.builder("executor.completed." + serviceName, threadPoolExecutor, pool -> Long.toString(pool.getCompletedTaskCount()))
                .description("The approximate total number of tasks that have completed execution").register(registry, backendProvider);

        Sensor.builder("executor.active." + serviceName, threadPoolExecutor, pool -> Integer.toString(pool.getActiveCount()))
                .description("The approximate number of threads that are actively executing tasks").register(registry, backendProvider);

        Sensor.builder("executor.queued." + serviceName, threadPoolExecutor, pool -> Integer.toString(pool.getQueue().size()))
                .description("The approximate number of tasks that are queued for execution").register(registry, backendProvider);

        Sensor.builder("executor.queue.remaining." + serviceName, threadPoolExecutor, pool -> Integer.toString(pool.getQueue().remainingCapacity()))
                .description("The number of additional elements that this queue can ideally accept without blocking").register(registry, backendProvider);

        Sensor.builder("executor.pool.size." + serviceName, threadPoolExecutor, pool -> Integer.toString(pool.getPoolSize()))
                .description("The current number of threads in the pool").register(registry, backendProvider);

        Sensor.builder("executor.pool.core." + serviceName, threadPoolExecutor, pool -> Integer.toString(pool.getCorePoolSize()))
                .description("The core number of threads for the pool").register(registry, backendProvider);

        Sensor.builder("executor.pool.max." + serviceName, threadPoolExecutor, pool -> Integer.toString(pool.getMaximumPoolSize()))
                .description("The maximum allowed number of threads in the pool").register(registry, backendProvider);

        return List.of("executor.completed." + serviceName, "executor.active." + serviceName, "executor.queued." + serviceName,
                "executor.queue.remaining." + serviceName, "executor.pool.size." + serviceName, "executor.pool.core." + serviceName,
                "executor.pool.max." + serviceName);
    }

    /**
     * --add-opens=java.base/java.util.concurrent=ALL-UNNAMED
     */
    private ThreadPoolExecutor unwrapThreadPoolExecutor(final ExecutorService executor, final Class<?> wrapper) {
        try {
            // java.util.concurrent.Executors.DelegatedExecutorService.e
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(wrapper, lookup);
            final VarHandle varHandle = privateLookup.findVarHandle(wrapper, "e", ExecutorService.class);
            return (ThreadPoolExecutor) varHandle.get(executor);

            //            final Field field = wrapper.getDeclaredField("e");
            //            field.setAccessible(true);
            //            return (ThreadPoolExecutor) field.get(executor);
        }
        catch (NoSuchFieldException | IllegalAccessException | RuntimeException ex) {
            // Cannot use InaccessibleObjectException since it was introduced in Java 9, so catch all RuntimeExceptions instead.
            // Do nothing. We simply can't get to the underlying ThreadPoolExecutor.
            getLogger().warn("Cannot unwrap ThreadPoolExecutor for monitoring from {} due to {}: {}", wrapper.getName(), ex.getClass().getName(), ex.getMessage());
        }

        return null;
    }
}
