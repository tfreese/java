package de.freese.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author Thomas Freese
 */
public final class CaffeineDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaffeineDemo.class);

    public static void main(final String[] args) {
        // Redirect Caffeine JUL to Slf4J.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try (ExecutorService executorService = Executors.newFixedThreadPool(3);
             ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3)) {
            final CacheLoader<String, String> cacheLoader = key -> {
                LOGGER.info("CacheLoader: {}", key);

                // if (System.currentTimeMillis() % 2 == 0) {
                //     throw new Exception("CacheLoader Exception");
                // }

                return key.toLowerCase() + "Value";
            };

            final LoadingCache<String, String> cache = Caffeine.newBuilder()
                    .expireAfterAccess(3, TimeUnit.SECONDS)
                    .maximumSize(100)
                    .weakKeys()
                    .weakValues()
                    .recordStats()
                    .executor(executorService)
                    .scheduler(Scheduler.forScheduledExecutorService(scheduledExecutorService))
                    .evictionListener((key, value, cause) -> LOGGER.info("Eviction: {} - {} = {}", cause, key, value))
                    .removalListener((key, value, cause) -> LOGGER.info("Removal: {} - {} = {}", cause, key, value))
                    .build(cacheLoader);

            cache.get("a");
            cache.get("a");
            cache.get("b");
            cache.put("c", "C"); // Triggert refreshAfterWrite - Removal: REPLACED

            final CacheStats cacheStats = cache.stats();

            LOGGER.info("hitCount: {}", cacheStats.hitCount());
            LOGGER.info("missCount: {}", cacheStats.missCount());
            LOGGER.info("hitRate: {}%", BigDecimal.valueOf(cacheStats.hitRate() * 100D).setScale(3, RoundingMode.HALF_UP).doubleValue());
            LOGGER.info("missRate: {}%", BigDecimal.valueOf(cacheStats.missRate() * 100D).setScale(3, RoundingMode.HALF_UP).doubleValue());

            TimeUnit.SECONDS.sleep(2);
            cache.get("c"); // Triggert Refresh, wenn Timeout vorbei ist.

            TimeUnit.SECONDS.sleep(3);
            cache.get("d");

            cache.invalidateAll();
            cache.cleanUp();

            // Time for cleanup.
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private CaffeineDemo() {
        super();
    }
}
