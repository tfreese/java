// Created: 27.05.2018
package de.freese.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://reflectoring.io/spring-boot-hazelcast">spring-boot-hazelcast</a>
 *
 * @author Thomas Freese
 */
// @SuppressWarnings("java:S1313")
public final class HazelcastJavaConfigNode1Demo {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastJavaConfigNode1Demo.class);

    public static void main(final String[] args) {
        final HazelcastInstance hazelcastInstance = getHazelcastInstance();

        final IMap<String, String> map = hazelcastInstance.getMap("test");
        // ReplicatedMap<String, String> map = hazelcastInstance.getReplicatedMap("test2");

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

    private static HazelcastInstance getHazelcastInstance() {
        final Config config = CacheConfigurer.configureHazelCastWithNetwork(5801);

        return Hazelcast.newHazelcastInstance(config);
    }

    private HazelcastJavaConfigNode1Demo() {
        super();
    }
}
