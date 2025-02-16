// Created: 11 Juni 2024
package de.freese.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Thomas Freese
 */
@SpringBootApplication(scanBasePackages = {"de.freese.cache"}, exclude = {HazelcastAutoConfiguration.class})
@EnableCaching
public class SpringCacheDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCacheDemo.class);

    @Component
    static class CacheRunner implements ApplicationRunner {
        @Resource
        @Qualifier("testCache")
        private Cache cache;

        // @Resource
        // private Cache testCache;

        // @Resource
        // private CacheManager cacheManager;

        @Override
        public void run(final ApplicationArguments args) throws Exception {
            // Cache cache = cacheManager.getCache("test");

            cache.put("a", "aValue");
            LOGGER.info("a = {}", cache.get("a", String.class));
            LOGGER.info("a = {}", cache.get("a", String.class));

            TimeUnit.SECONDS.sleep(4);

            LOGGER.info("a = {}", cache.get("a", String.class));

            cache.put("b", "bValue");

            final CacheStats cacheStats = ((CaffeineCache) cache).getNativeCache().stats();
            LOGGER.info("hitCount: {}", cacheStats.hitCount());
            LOGGER.info("missCount: {}", cacheStats.missCount());
            LOGGER.info("hitRate: {}%", BigDecimal.valueOf(cacheStats.hitRate() * 100D).setScale(3, RoundingMode.HALF_UP).doubleValue());
            LOGGER.info("missRate: {}%", BigDecimal.valueOf(cacheStats.missRate() * 100D).setScale(3, RoundingMode.HALF_UP).doubleValue());

            // @see destroyMethod testCache
            // cache.invalidate();
            // cache.clear();
        }
    }

    public static void main(final String[] args) {
        // Redirect Caffeine JUL to Slf4J.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        SpringApplication.run(SpringCacheDemo.class, args);

        // new SpringApplicationBuilder(SpringCacheDemo.class)
        //         .web(WebApplicationType.NONE)
        //         .registerShutdownHook(true)
        //         .run(args);
    }

    @Bean
    public CacheManager cacheManager() {
        // final ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        // cacheManager.setAllowNullValues(true);
        // // cacheManager.setCacheNames(List.of("userCache")); // Disable dynamic creation of Caches.
        //
        // return cacheManager;

        return new CacheManager() {
            private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

            @Override
            public Cache getCache(final String name) {
                return cacheMap.computeIfAbsent(name, nameKey -> {
                    final Caffeine<Object, Object> caffeine;

                    if ("test".equals(name)) {
                        caffeine = Caffeine.from("maximumSize=10,expireAfterAccess=3s,recordStats");
                    }
                    else {
                        caffeine = Caffeine.newBuilder()
                                .maximumSize(1000)
                                .expireAfterAccess(Duration.ofHours(12))
                        ;
                    }

                    final com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache = caffeine
                            .evictionListener((key, value, cause) -> LOGGER.info("Eviction: {} - {} = {}", cause, key, value))
                            .removalListener((key, value, cause) -> LOGGER.info("Removal: {} - {} = {}", cause, key, value))
                            .build();

                    return new CaffeineCache(name, caffeineCache, true);
                });
            }

            @Override
            public Collection<String> getCacheNames() {
                return Set.copyOf(cacheMap.keySet());
            }
        };
    }

    @Bean(destroyMethod = "clear")
    public Cache testCache(final CacheManager cacheManager) {
        return cacheManager.getCache("test");
    }

    // @Bean
    // public CacheManagerCustomizer<CaffeineCacheManager> cacheManagerCustomizer() {
    //     return cacheManager -> cacheManager.setAllowNullValues(false);
    // }

    // @Bean
    // UserCache userCache(final CacheManager cacheManager) {
    //     final Cache cache = cacheManager.getCache("userCache");
    //
    //     return new SpringCacheBasedUserCache(cache);
    // }
}
