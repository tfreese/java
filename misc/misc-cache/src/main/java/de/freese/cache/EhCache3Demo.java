// Created: 27.05.2018
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * @author Thomas Freese
 */
public final class EhCache3Demo {
    private static final Logger LOGGER = LoggerFactory.getLogger(EhCache3Demo.class);

    public static void main(final String[] args) throws Exception {
        final URL configUrl = ClassLoader.getSystemResource("ehcache3.xml");
        final Configuration xmlConfig = new XmlConfiguration(configUrl);

        final AtomicBoolean runner = new AtomicBoolean(true);

        try (CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
             ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            cacheManager.init();

            final Cache<String, String> cache = cacheManager.getCache("defaultCache", String.class, String.class);

            if (cache == null) {
                LOGGER.error("Cache not exist");
                return;
            }

            executorService.execute(() -> {
                int counter = 0;

                while (runner.get()) {
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
    }

    private EhCache3Demo() {
        super();
    }
}
