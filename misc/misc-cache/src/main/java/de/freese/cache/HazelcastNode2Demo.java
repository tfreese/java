// Created: 27.05.2018
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class HazelcastNode2Demo {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastNode2Demo.class);

    public static void main(final String[] args) throws Exception {
        final URL configUrl = ClassLoader.getSystemResource("hazelcast-node2.xml");
        final Config config = new XmlConfigBuilder(configUrl).build();
        // config.setProperty("hazelcast.partition.count", "2");

        final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        final IMap<String, String> map = hazelcastInstance.getMap("test");
        // ReplicatedMap<String, String> map = hazelcastInstance.getReplicatedMap("test1");

        final AtomicBoolean runner = new AtomicBoolean(true);

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.execute(() -> {
                int counter = 0;

                while (runner.get()) {
                    final String value = map.get("key");
                    LOGGER.info("HazelcastNode1Main: {}: cache value = {}", Thread.currentThread().getName(), value);

                    if (value == null) {
                        map.put("key", "value" + counter);
                        counter++;
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

            // Block main-Thread.
            System.console().readLine();

            runner.set(false);

            CacheConfigurer.sleep(1500L);
        }

        hazelcastInstance.shutdown();
        Hazelcast.shutdownAll();
    }

    private HazelcastNode2Demo() {
        super();
    }
}
