// Created: 28.05.2018
package de.freese.cache;

import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import com.hazelcast.cache.HazelcastCachingProvider;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://github.com/hazelcast/hazelcast-code-samples">hazelcast-code-samples</a><br>
 * <a href="http://docs.hazelcast.org/docs/latest-dev/manual/html-single/index.html">latest-dev</a>
 *
 * @author Thomas Freese
 */
public final class HazelcastJCacheDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastJCacheDemo.class);

    static void main() throws Exception {
        System.setProperty("hazelcast.map.partition.count", "1");

        final URL configUrl = ClassLoader.getSystemResource("hazelcast.xml");

        final Config config = new XmlConfigBuilder(configUrl).build();

        Hazelcast.newHazelcastInstance(config);

        final Properties properties = HazelcastCachingProvider.propertiesByInstanceName("my-test-instance");

        // By default, the delegating caching provider chooses the client-side implementation.
        System.setProperty("hazelcast.jcache.provider.type", "member");
        // System.setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER, "com.hazelcast.cache.HazelcastMemberCachingProvider");

        final AtomicBoolean runner = new AtomicBoolean(true);

        // "com.hazelcast.cache.HazelcastMemberCachingProvider"
        // com.hazelcast.client.cache.HazelcastClientCachingProvider
        // com.hazelcast.cache.HazelcastCachingProvider
        try (CachingProvider cachingProvider = Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider");
             // CacheManager cacheManager = cachingProvider.getCacheManager(configUrl.toURI(), null);
             CacheManager cacheManager = cachingProvider.getCacheManager(null, null, properties);
             ExecutorService executorService = Executors.newSingleThreadExecutor()) {

            // final CacheConfig<String, String> cacheConfig = new CacheConfig<String, String>()
            //         .setEvictionConfig(new EvictionConfig()
            //                 .setSize(3)
            //                 .setMaxSizePolicy(MaxSizePolicy.ENTRY_COUNT)
            //                 .setEvictionPolicy(EvictionPolicy.LRU)
            //         )
            //         .setInMemoryFormat(InMemoryFormat.OBJECT);

            // final CompleteConfiguration<String, String> cacheConfig =
            //         new MutableConfiguration<String, String>()
            //                 .setTypes(String.class, String.class)
            //                 .setExpiryPolicyFactory(
            //                         AccessedExpiryPolicy.factoryOf(Duration.ONE_MINUTE)
            //                 );
            // final Cache<String, String> cache = cacheManager.createCache("test-cache2", cacheConfig);
            final Cache<String, String> cache = cacheManager.getCache("test-cache");

            executorService.execute(() -> {
                int counter = 0;

                while (runner.get()) {
                    // for (final Cache.Entry<String, String> entry : cache) {
                    //     LOGGER.info("entry = {}", entry);
                    // }

                    final String value = cache.get("key");
                    LOGGER.info("{}: cache value = {}", Thread.currentThread().getName(), value);

                    if (value == null) {
                        cache.put("key", "value" + counter);
                        counter++;
                    }

                    if (counter == 1) {
                        cache.put("key1", "value1");
                        cache.put("key2", "value2");
                        cache.put("key3", "value3");
                        cache.remove("key2");
                    }

                    CacheConfigurer.sleep(1000L);
                }
            });

            // Block main-Thread.
            System.console().readLine();

            runner.set(false);

            CacheConfigurer.sleep(1500L);
        }

        // hazelcastInstance.shutdown();
        Hazelcast.shutdownAll();
    }

    private HazelcastJCacheDemo() {
        super();
    }
}
