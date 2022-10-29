// Created: 03.09.2021
package de.freese.jsensors.binder;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

import de.freese.jsensors.registry.SensorRegistry;
import de.freese.jsensors.sensor.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics
 *
 * @author Thomas Freese
 */
public class ExecutorServiceMetrics implements SensorBinder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceMetrics.class);

    private final ExecutorService executorService;

    private final String executorServiceName;

    public ExecutorServiceMetrics(final ExecutorService executorService, final String executorServiceName)
    {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.executorServiceName = Objects.requireNonNull(executorServiceName, "executorServiceName required");
    }

    /**
     * @see de.freese.jsensors.binder.SensorBinder#bindTo(de.freese.jsensors.registry.SensorRegistry)
     */
    @Override
    public void bindTo(final SensorRegistry registry)
    {
        if (this.executorService instanceof ForkJoinPool fjp)
        {
            bindTo(registry, fjp);
        }
        else if (this.executorService instanceof ThreadPoolExecutor tpe)
        {
            bindTo(registry, tpe);
        }
        else
        {
            String className = this.executorService.getClass().getName();
            ThreadPoolExecutor pool = null;

            if ("java.util.concurrent.Executors$DelegatedScheduledExecutorService".equals(className))
            {
                pool = unwrapThreadPoolExecutor(this.executorService, this.executorService.getClass());
            }
            else if ("java.util.concurrent.Executors$FinalizableDelegatedExecutorService".equals(className))
            {
                pool = unwrapThreadPoolExecutor(this.executorService, this.executorService.getClass().getSuperclass());
            }

            if (pool != null)
            {
                bindTo(registry, pool);
            }
            else
            {
                // getLogger().warn("executorService not supported: {}", className);
                throw new IllegalArgumentException(String.format("executorService not supported: '%s'", className));
            }
        }
    }

    protected Logger getLogger()
    {
        return LOGGER;
    }

    private void bindTo(final SensorRegistry registry, final ForkJoinPool forkJoinPool)
    {
        Sensor.builder("executor.steals." + this.executorServiceName, forkJoinPool, pool -> Long.toString(pool.getStealCount()))
                .description("Estimate of the total number of tasks stolen from one thread's work queue by another. The reported value "
                        + "underestimates the actual total number of steals when the pool is not quiescent")
                .register(registry);

        Sensor.builder("executor.queued." + this.executorServiceName, forkJoinPool, pool -> Long.toString(pool.getQueuedTaskCount()))
                .description("An estimate of the total number of tasks currently held in queues by worker threads").register(registry);

        Sensor.builder("executor.active." + this.executorServiceName, forkJoinPool, pool -> Integer.toString(pool.getActiveThreadCount()))
                .description("An estimate of the number of threads that are currently stealing or executing tasks").register(registry);

        Sensor.builder("executor.running." + this.executorServiceName, forkJoinPool, pool -> Integer.toString(pool.getRunningThreadCount()))
                .description(
                        "An estimate of the number of worker threads that are not blocked waiting to join tasks or for other managed synchronization threads")
                .register(registry);
    }

    private void bindTo(final SensorRegistry registry, final ThreadPoolExecutor threadPoolExecutor)
    {
        Sensor.builder("executor.completed." + this.executorServiceName, threadPoolExecutor, pool -> Long.toString(pool.getCompletedTaskCount()))
                .description("The approximate total number of tasks that have completed execution").register(registry);

        Sensor.builder("executor.active." + this.executorServiceName, threadPoolExecutor, pool -> Integer.toString(pool.getActiveCount()))
                .description("The approximate number of threads that are actively executing tasks").register(registry);

        Sensor.builder("executor.queued." + this.executorServiceName, threadPoolExecutor, pool -> Integer.toString(pool.getQueue().size()))
                .description("The approximate number of tasks that are queued for execution").register(registry);

        Sensor.builder("executor.queue.remaining." + this.executorServiceName, threadPoolExecutor,
                        pool -> Integer.toString(pool.getQueue().remainingCapacity()))
                .description("The number of additional elements that this queue can ideally accept without blocking").register(registry);

        Sensor.builder("executor.pool.size." + this.executorServiceName, threadPoolExecutor, pool -> Integer.toString(pool.getPoolSize()))
                .description("The current number of threads in the pool").register(registry);

        Sensor.builder("executor.pool.core." + this.executorServiceName, threadPoolExecutor, pool -> Integer.toString(pool.getCorePoolSize()))
                .description("The core number of threads for the pool").register(registry);

        Sensor.builder("executor.pool.max." + this.executorServiceName, threadPoolExecutor, pool -> Integer.toString(pool.getMaximumPoolSize()))
                .description("The maximum allowed number of threads in the pool").register(registry);
    }

    private ThreadPoolExecutor unwrapThreadPoolExecutor(final ExecutorService executor, final Class<?> wrapper)
    {
        try
        {
            Field f = wrapper.getDeclaredField("e");
            f.setAccessible(true);

            return (ThreadPoolExecutor) f.get(executor);
        }
        catch (NoSuchFieldException | IllegalAccessException | RuntimeException ex)
        {
            // Cannot use InaccessibleObjectException since it was introduced in Java 9, so catch all RuntimeExceptions instead
            // Do nothing. We simply can't get to the underlying ThreadPoolExecutor.
            getLogger().warn("Cannot unwrap ThreadPoolExecutor for monitoring from {} due to {}: {}", wrapper.getName(), ex.getClass().getName(),
                    ex.getMessage());
        }

        return null;
    }
}
