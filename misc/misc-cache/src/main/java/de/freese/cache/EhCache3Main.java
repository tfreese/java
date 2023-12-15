// Created: 27.05.2018
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

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
public final class EhCache3Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(EhCache3Main.class);

    public static void main(final String[] args) throws Exception {
        final URL configUrl = ClassLoader.getSystemResource("ehcache3.xml");
        final Configuration xmlConfig = new XmlConfiguration(configUrl);

        try (CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig)) {
            cacheManager.init();

            final Cache<String, String> cache = cacheManager.getCache("defaultCache", String.class, String.class);

            if (cache == null) {
                LOGGER.error("Cache not exist");
                return;
            }

            ForkJoinPool.commonPool().execute(() -> {
                while (true) {
                    final String value = cache.get("key");
                    LOGGER.info("{}: cache value = {}}", Thread.currentThread().getName(), value);

                    if (value == null) {
                        cache.put("key", "value");
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            });

            // main-Thread blockieren.
            System.in.read();
        }
    }

    private EhCache3Main() {
        super();
    }
}
