// Created: 11 Apr. 2025
package de.freese.gradle.cache;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Resource;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import de.freese.gradle.cache.storage.Storage;

/**
 * <a href="https://github.com/sinwe/http-gradle-cache-server/tree/master">http-gradle-cache-server</a>
 *
 * @author Thomas Freese
 */
@SpringBootApplication
@EnableScheduling
public class GradleBuildCache {
    static void main(final String[] args) {
        SpringApplication.run(GradleBuildCache.class, args);
    }

    @Value("${entries.timeout:-}")
    @Nullable
    private Duration entriesTimeout;

    @Resource
    @Nullable
    private Storage storage;

    // CronExpression
    @Scheduled(cron = "${jobs.clean.cron:-}")
    // @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelayString = "${jobs.clean.delay:3}")
    public void cleanStorage() {
        if (entriesTimeout != null && storage != null) {
            // final Instant instant = Instant.now().minus(3, ChronoUnit.DAYS);
            final Instant instant = Instant.now().minus(entriesTimeout);

            storage.removeOlderThan(instant);
        }
    }
}
