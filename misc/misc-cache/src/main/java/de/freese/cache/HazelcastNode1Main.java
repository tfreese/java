// Created: 27.05.2018
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
public final class HazelcastNode1Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastNode1Main.class);

    public static void main(final String[] args) throws Exception {
        final URL configUrl = ClassLoader.getSystemResource("hazelcast-node1.xml");
        final Config config = new XmlConfigBuilder(configUrl).build();
        // config.setProperty("hazelcast.partition.count", "271");

        final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        // Map ist niemals null.
        final IMap<String, String> map = hazelcastInstance.getMap("test");
        // ReplicatedMap<String, String> map = hazelcastInstance.getReplicatedMap("test1");

        final AtomicInteger atomicInteger = new AtomicInteger(0);

        ForkJoinPool.commonPool().execute(() -> {
            while (true) {
                final String value = map.get("key");
                LOGGER.info("HazelcastNode1Main: {}: cache value = {}", Thread.currentThread().getName(), value);

                if (value == null) {
                    map.put("key", "value" + atomicInteger.getAndIncrement());
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

    private HazelcastNode1Main() {
        super();
    }
}
