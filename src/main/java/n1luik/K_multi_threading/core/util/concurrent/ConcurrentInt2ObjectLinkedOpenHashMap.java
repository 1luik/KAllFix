package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentInt2ObjectLinkedOpenHashMap<V> extends Int2ObjectLinkedOpenHashMap<V> {

    Map<Integer, V> backing;

    public ConcurrentInt2ObjectLinkedOpenHashMap() {
        backing = new ConcurrentHashMap<Integer, V>();
    }

    @Override
    public V get(int key) {
        return backing.get(key);
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return backing.containsValue(value);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> m) {
        backing.putAll(m);
    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public void defaultReturnValue(V rv) {
        throw new NotImplementedException("MCMT - Not implemented");
    }

    @Override
    public V defaultReturnValue() {
        return null;
    }

    @Override
    public FastSortedEntrySet<V> int2ObjectEntrySet() {
        return FastUtilHackUtil.entrySetFastSortedIntWrap(backing);
    }


    @Override
    public IntSortedSet keySet() {
        return FastUtilHackUtil.wrapIntSet(backing.keySet());
    }

    @Override
    public ObjectCollection<V> values() {
        return FastUtilHackUtil.wrap(backing.values());
    }

    @Override
    public boolean containsKey(int key) {
        return backing.containsKey(key);
    }

    @Override
    public V put(int key, V value) {
        return backing.put(key, value);
    }

    @Override
    public V put(Integer key, V value) {
        return backing.put(key, value);
    }

    @Override
    public V remove(int key) {
        return backing.remove(key);
    }

    @Override
    public Int2ObjectSortedMap<V> subMap(int from, int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2ObjectSortedMap<V> headMap(int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2ObjectSortedMap<V> tailMap(int from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int firstIntKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIntKey() {
        throw new UnsupportedOperationException();
    }
}
