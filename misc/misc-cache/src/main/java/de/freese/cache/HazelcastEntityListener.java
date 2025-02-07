// Created: 07 Feb. 2025
package de.freese.cache;

import java.util.Objects;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.map.MapEvent;
import com.hazelcast.map.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class HazelcastEntityListener<K, V> implements EntryListener<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastEntityListener.class);

    private final MapStore<K, V> mapStore;

    public HazelcastEntityListener(final MapStore<K, V> mapStore) {
        super();

        this.mapStore = Objects.requireNonNull(mapStore, "mapStore required");
    }

    @Override
    public void entryAdded(final EntryEvent<K, V> event) {
        LOGGER.info("entryAdded: {}", event);
    }

    @Override
    public void entryEvicted(final EntryEvent<K, V> event) {
        LOGGER.info("entryEvicted: {}", event);

        mapStore.delete(event.getKey());
    }

    @Override
    public void entryExpired(final EntryEvent<K, V> event) {
        LOGGER.info("entryExpired: {}", event);

        mapStore.delete(event.getKey());
    }

    @Override
    public void entryRemoved(final EntryEvent<K, V> event) {
        LOGGER.info("entryRemoved: {}", event);
    }

    @Override
    public void entryUpdated(final EntryEvent<K, V> event) {
        LOGGER.info("entryUpdated: {}", event);
    }

    @Override
    public void mapCleared(final MapEvent event) {
        LOGGER.info("mapCleared: {}", event);
    }

    @Override
    public void mapEvicted(final MapEvent event) {
        LOGGER.info("mapEvicted: {}", event);
    }

}
