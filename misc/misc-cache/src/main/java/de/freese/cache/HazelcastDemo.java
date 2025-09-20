// Created: 28.05.2018
package de.freese.cache;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
public final class HazelcastDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastDemo.class);

    static void main() throws IOException {
        System.setProperty("hazelcast.map.partition.count", "1");

        final URL configUrl = ClassLoader.getSystemResource("hazelcast.xml");

        final Config config = new XmlConfigBuilder(configUrl).build();

        final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        final IMap<String, String> map = hazelcastInstance.getMap("test");

        final AtomicBoolean runner = new AtomicBoolean(true);

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.execute(() -> {
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

            // Block main-Thread.
            System.console().readLine();

            runner.set(false);

            CacheConfigurer.sleep(1500L);
        }

        hazelcastInstance.shutdown();
        Hazelcast.shutdownAll();
    }

    private HazelcastDemo() {
        super();
    }
}
