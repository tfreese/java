// Created: 28.05.2018
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://github.com/hazelcast/hazelcast-code-samples">hazelcast-code-samples</a><br>
 * <a href="http://docs.hazelcast.org/docs/latest-dev/manual/html-single/index.html">latest-dev</a>
 *
 * @author Thomas Freese
 */
public final class HazelcastMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastMain.class);

    public static void main(final String[] args) throws Exception {
        System.setProperty("hazelcast.map.partition.count", "1");

        final URL configUrl = ClassLoader.getSystemResource("hazelcast.xml");

        // By default, the delegating caching provider chooses the client-side implementation.
        System.setProperty("hazelcast.jcache.provider.type", "member");

        // com.hazelcast.client.cache.HazelcastClientCachingProvider
        try (CachingProvider cachingProvider = Caching.getCachingProvider("com.hazelcast.cache.HazelcastMemberCachingProvider");
             CacheManager cacheManager = cachingProvider.getCacheManager(configUrl.toURI(), null)
             // CacheManager cacheManager = cachingProvider.getCacheManager()
        ) {
            // final CompleteConfiguration<String, String> config =
            //         new MutableConfiguration<String, String>()
            //                 .setTypes(String.class, String.class);
            // final Cache<String, String> cache = cacheManager.createCache("test", config);
            final Cache<String, String> cache = cacheManager.getCache("test", String.class, String.class);

            for (int i = 0; i < 10; i++) {
                final String value = cache.get("key");
                LOGGER.info("{}: cache value = {}", Thread.currentThread().getName(), value);

                if (value == null) {
                    cache.put("key", "value");
                }

                LOGGER.info("{}: cache value = {}", Thread.currentThread().getName(), cache.get("key"));

                CacheConfigurer.sleep(1000L);
            }
        }

        final Config config = new XmlConfigBuilder(configUrl).build();

        final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        // Map ist niemals null.
        final IMap<String, String> map = hazelcastInstance.getMap("test");

        final AtomicBoolean runner = new AtomicBoolean(true);

        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.execute(() -> {
                int counter = 0;

                while (runner.get()) {
                    final String value = map.get("key");
                    LOGGER.info("{}: cache value = {}", Thread.currentThread().getName(), value);

                    if (value == null) {
                        map.put("key", "value" + counter);
                        counter++;
                    }

                    if (counter == 1) {
                        map.put("key1", "value1");
                        map.put("key2", "value2");
                        map.put("key3", "value3");
                        map.remove("key2");
                    }

                    CacheConfigurer.sleep(1000L);
                }
            });

            // main-Thread blockieren.
            System.console().readLine();

            runner.set(false);

            CacheConfigurer.sleep(1500L);
        }

        hazelcastInstance.shutdown();
        Hazelcast.shutdownAll();
    }

    private HazelcastMain() {
        super();
    }
}
