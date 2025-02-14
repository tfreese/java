// Created: 07 Feb. 2025
package de.freese.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.jet.config.JetConfig;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class CacheConfigurer {
    public static Config configureHazelCastWithNetwork(final int localPort) {
        return configureHazelCastWithDefaults().setNetworkConfig(new NetworkConfig()
                .setPort(localPort)
                .setPortAutoIncrement(false)
                .setReuseAddress(true)
                .setInterfaces(new InterfacesConfig()
                        .setEnabled(true)
                        .addInterface("192.168.155.100")
                )
                .setJoin(new JoinConfig()
                        .setMulticastConfig(new MulticastConfig()
                                .setEnabled(true)
                                .setMulticastGroup("224.2.2.3")
                                .setMulticastPort(54327)
                                .setMulticastTimeToLive(32)
                                .setMulticastTimeoutSeconds(3)
                                .setTrustedInterfaces(Set.of("192.168.155.100"))
                        )
                        .setTcpIpConfig(new TcpIpConfig()
                                .setEnabled(false) // Either Multicast or TCP.
                        )
                )
        );
    }

    public static Config configureHazelCastWithoutNetwork() {
        return configureHazelCastWithDefaults().setNetworkConfig(new NetworkConfig()
                .setInterfaces(new InterfacesConfig().setEnabled(false))
                .setJoin(new JoinConfig()
                        .setMulticastConfig(new MulticastConfig().setEnabled(false))
                        .setTcpIpConfig(new TcpIpConfig().setEnabled(false))
                        .setAwsConfig(new AwsConfig().setEnabled(false))
                )
        );
    }

    public static void sleep(final long millies) {
        try {
            TimeUnit.MILLISECONDS.sleep(millies);
        }
        catch (InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();

            LoggerFactory.getLogger(CacheConfigurer.class).error(ex.getMessage(), ex);
        }
    }

    private static Config configureHazelCastParameter(final Config config) {
        // Enable back pressure.
        // Avoid overload of the cluster.
        return config.setProperty("hazelcast.backpressure.enabled", Boolean.TRUE.toString())

                // Defines cache invalidation event batch sending frequency in seconds
                .setProperty("hazelcast.cache.invalidation.batchfrequency.seconds", "15")

                // Number of threads that the client engine has available for processing requests that are blocking, e.g., transactions.
                // When not set, it is set as the value of core size * 20.
                .setProperty("hazelcast.clientengine.blocking.thread.count", "2")

                // Number of threads to process query requests coming from the clients.
                // Default count is the number of cores multiplied by 1.
                .setProperty("hazelcast.clientengine.query.thread.count", "2")

                // Maximum number of threads to process non-partition-aware client requests, like map.size(), executor tasks, etc.
                // Default count is the number of cores multiplied by 20.
                .setProperty("hazelcast.clientengine.thread.count", "2")

                // Number of event handler threads.
                .setProperty("hazelcast.event.thread.count", "2")

                // Maximum wait in seconds during graceful shutdown.
                .setProperty("hazelcast.graceful.shutdown.max.wait", "10")

                // Number of socket input threads.
                .setProperty("hazelcast.io.input.thread.count", "2")

                // Number of socket output threads.
                .setProperty("hazelcast.io.output.thread.count", "2")

                // Number of threads performing socket input and socket output.
                // If, for example, the default value (3) is used, it means there
                // are 3 threads performing input and 3 threads performing output (6 threads in total).
                .setProperty("hazelcast.io.thread.count", "2")

                // Name of logging framework type to send logging events.
                .setProperty("hazelcast.logging.type", "slf4j")

                // If the maximum number of invocations has been reached, Hazelcast automatically applies an exponential backoff policy.
                // This gives the system some time to deal with the load. Using the following system property, you can configure the maximum
                // time to wait before a HazelcastOverloadException is thrown.
                .setProperty("hazelcast.operation.backup.timeout.millis", "60000")

                // Number of generic operation handler threads for each Hazelcast member.
                // Its default value is the maximum of 2 and processor count / 2.
                .setProperty("hazelcast.operation.generic.thread.count", "2")

                // Number of priority generic operation handler threads per member.
                // Having at least 1 priority generic operation thread helps to improve cluster
                // stability since a lot of cluster operations are generic priority operations,
                // and they should get executed as soon as possible.
                // If there is a dedicated generic operation thread then these operations
                // don’t get delayed because the generic threads are busy executing regular
                // user operations.
                // So unless memory consumption is an issue, make sure there is at least 1 thread.
                .setProperty("hazelcast.operation.priority.generic.thread.count", "2")

                // Number of threads the process responses.
                // The default value gives stable and good performance.
                // If set to 0, the response threads are bypassed and the response handling is done on the IO threads.
                // Under certain conditions this can give a higher throughput.
                .setProperty("hazelcast.operation.response.thread.count", "0")

                // Number of partition based operation handler threads for each Hazelcast member.
                // Its default value is the maximum of 2 and count of available processors.
                .setProperty("hazelcast.operation.thread.count", "2")

                // Total partition count, Default 271.
                .setProperty("hazelcast.partition.count", "1")

                // When this is enabled, this thread terminates the Hazelcast instance without waiting to shut down gracefully.
                .setProperty("hazelcast.shutdownhook.enabled", Boolean.FALSE.toString())

                // TERMINATE / GRACEFUL
                .setProperty("hazelcast.shutdownhook.policy", "GRACEFUL");
    }

    private static Config configureHazelCastWithDefaults() {
        final HazelcastMapStore<String, String> mapStore = new HazelcastMapStore<>();
        final HazelcastEntityListener<String, String> entryListener = new HazelcastEntityListener<>(mapStore);

        final MapConfig mapConfig = new MapConfig("default")
                .setTimeToLiveSeconds(5)
                .setMaxIdleSeconds(3)
                .setEvictionConfig(new EvictionConfig()
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
                        .setSize(3)
                )
                .setStatisticsEnabled(false)
                .setInMemoryFormat(InMemoryFormat.BINARY)
                .setMapStoreConfig(new MapStoreConfig().setImplementation(mapStore))
                .addEntryListenerConfig(new EntryListenerConfig(entryListener, true, true))
                // .setCacheDeserializedValues(CacheDeserializedValues.INDEX_ONLY)

                // 2nd Level Cache, only for HazelcastClients.
                // .setNearCacheConfig(new NearCacheConfig("test")
                //         .setInMemoryFormat(InMemoryFormat.OBJECT)
                //         .setTimeToLiveSeconds(5)
                //         .setMaxIdleSeconds(60)
                // )
                ;

        return configureHazelCastParameter(new Config()
                .addMapConfig(mapConfig))
                .setClusterName("my-test")
                .setInstanceName("my-test-instance")
                // .setPartitionGroupConfig(new PartitionGroupConfig().setEnabled(false))
                .setJetConfig(new JetConfig().setEnabled(true))
                ;
    }

    private CacheConfigurer() {
        super();
    }
}
