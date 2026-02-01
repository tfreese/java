// Created: 11 Apr. 2025
package de.freese.gradle.cache.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import de.freese.gradle.cache.storage.CaffeineStorage;
import de.freese.gradle.cache.storage.FileStorage;
import de.freese.gradle.cache.storage.MapStorage;
import de.freese.gradle.cache.storage.Storage;

/**
 * Configuration.
 *
 * @author Thomas Freese
 */
@Configuration
public class CacheConfig {
    @Bean
    @ConditionalOnMissingBean({Executor.class, ExecutorService.class})
    @Primary
    public ThreadPoolExecutorFactoryBean executorService() {
        final int coreSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 4);
        final int maxSize = coreSize * 2;
        final int queueSize = maxSize * 4;
        final int keepAliveSeconds = 60;

        final ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setQueueCapacity(queueSize);
        bean.setKeepAliveSeconds(keepAliveSeconds);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("executor-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(ScheduledExecutorService.class)
    public ScheduledExecutorFactoryBean scheduledExecutorService() {
        final int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 4);

        final ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(poolSize);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("scheduler-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * Required for {@link EnableAsync}.
     */
    @Bean({"taskExecutor", "asyncTaskExecutor"})
    @ConditionalOnMissingBean({AsyncTaskExecutor.class, TaskExecutor.class})
    public AsyncTaskExecutor springTaskExecutor(final ExecutorService executorService) {
        return new ConcurrentTaskExecutor(executorService);
        // return new ConcurrentTaskExecutor(executorService().getObject());
    }

    /**
     * Required for  {@link EnableScheduling}.
     */
    @Bean("taskScheduler")
    @ConditionalOnMissingBean(TaskScheduler.class)
    public TaskScheduler springTaskScheduler(@Qualifier("executorService") final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService) {
        return new ConcurrentTaskScheduler(executorService, scheduledExecutorService);
    }

    @Profile("caffeine")
    @Bean("storage")
    Storage storageCaffeine(@Value("${entries.timeout}") final Duration entriesTimeout, final ExecutorService executorService,
                            final ScheduledExecutorService scheduledExecutorService) {
        return new CaffeineStorage(entriesTimeout, scheduledExecutorService, executorService);
    }

    @Profile("file")
    @Bean("storage")
    Storage storageFile() {
        return new FileStorage(Path.of(System.getProperty("java.io.tmpdir"), "build-cache"));
    }

    @Profile({"memory", "default"})
    @Bean("storage")
    Storage storageMap() {
        return new MapStorage();
    }
}
