// Created: 28.05.2018
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

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
        //// System.setProperty("hazelcast.jcache.provider.type", "member");

        // try (CachingProvider cachingProvider = Caching.getCachingProvider("com.hazelcast.cache.HazelcastMemberCachingProvider");
        //      CacheManager cacheManager = cachingProvider.getCacheManager(configUrl.toURI(), null)
        // ) {
        //     final Cache<String, String> cache = cacheManager.getCache("test", String.class, String.class);
        //
        //     final String value = cache.get("key");
        //     LOGGER.info("{}: cache value = {}", Thread.currentThread().getName(), value);
        //
        //     if (value == null) {
        //         cache.put("key", "value");
        //     }
        //
        //     LOGGER.info("{}: cache value = {}", Thread.currentThread().getName(), cache.get("key"));
        // }

        final Config config = new XmlConfigBuilder(configUrl).build();
        // config.setProperty("hazelcast.partition.count", "271");

        final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        // Map ist niemals null.
        final IMap<String, String> map = hazelcastInstance.getMap("test");

        ForkJoinPool.commonPool().execute(() -> {
            while (true) {
                final String value = map.get("key");
                LOGGER.info("{}: cache value = {}", Thread.currentThread().getName(), value);

                if (value == null) {
                    map.put("key", "value");
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
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
        });

        // main-Thread blockieren.
        System.in.read();

        hazelcastInstance.shutdown();
        Hazelcast.shutdownAll();
    }

    private HazelcastMain() {
        super();
    }
}
