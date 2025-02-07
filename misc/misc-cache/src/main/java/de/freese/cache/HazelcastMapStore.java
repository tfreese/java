// Created: 07 Feb. 2025
package de.freese.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hazelcast.map.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class HazelcastMapStore<K, V> implements MapStore<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastMapStore.class);

    private final Map<K, V> map = new HashMap<>();

    @Override
    public void delete(final K key) {
        LOGGER.info("mapStore-delete: {}", key);
        map.remove(key);
    }

    @Override
    public void deleteAll(final Collection<K> keys) {
        LOGGER.info("mapStore-deleteAll");
        map.clear();
    }

    @Override
    public V load(final K key) {
        LOGGER.info("mapStore-load; {}", key);
        return map.get(key);
    }

    @Override
    public Map<K, V> loadAll(final Collection<K> keys) {
        LOGGER.info("mapStore-loadAll");
        return map;
    }

    @Override
    public Iterable<K> loadAllKeys() {
        LOGGER.info("mapStore-loadAllKeys");
        return map.keySet();
    }

    @Override
    public void store(final K key, final V value) {
        LOGGER.info("mapStore-store: {}/{}", key, value);
        map.put(key, value);
    }

    @Override
    public void storeAll(final Map<K, V> map) {
        LOGGER.info("mapStore-storeAll");
        this.map.putAll(map);
    }
}
