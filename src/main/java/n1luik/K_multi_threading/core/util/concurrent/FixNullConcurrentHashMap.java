package n1luik.K_multi_threading.core.util.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 半兼容null的ConcurrentHashMap
 * */
public class FixNullConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
    public FixNullConcurrentHashMap() {

    }
    public FixNullConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }
    public FixNullConcurrentHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
    public FixNullConcurrentHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public FixNullConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    public V put( K key, V value) {
        if (key == null || value == null) return null;
        return super.put(key, value);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) return false;
        return super.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return super.entrySet();
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (key == null || value == null) return null;
        return super.merge(key, value, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null) return null;
        return super.compute(key, remappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null) return null;
        return super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (key == null) return null;
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        return super.containsKey(key);
    }

    @Override
    public V remove(Object key) {
        if (key == null) return null;
        return super.remove(key);
    }

    @Override
    public V replace(K key, V value) {
        if (key == null || value == null) return null;
        return super.replace(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null) return false;
        return super.replace(key, oldValue, newValue);
    }
}
