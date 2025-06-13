package n1luik.K_multi_threading.core.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class NodeHashMap<K,V> implements Map<K,V> {
    protected List<Entry<K,V>> entrySet = new ArrayList<>();
    protected Object[] rootNodes = new Object[16];
    protected Object[] rootNodeValues = new Object[16];

    public static <T> T nodesNodeGetter_B4(Object[] a,int id,int bitPos){
        int nid = (id >> bitPos) & 0xf;
        if (a[nid] == null)
            return null;
        return bitPos >= 27 ? (T)a[nid] : nodesNodeGetter_B4((Object[]) a[nid],id, bitPos + 4);
    }

    public static <T> T nodesNodeSetter_B4(Object[] a,int id,int bitPos,T t){
        int nid = (id >> bitPos) & 0xf;
        if (a[nid] == null) {
            if (bitPos < 27) {
                nodesNodeSetter_B4((Object[]) (a[nid] = new Object[16]), id, bitPos + 4, t);
            }else {
                a[nid] = t;
            }
            return null;
        }
        T r = bitPos >= 27 ? (T)a[nid] : nodesNodeSetter_B4((Object[]) a[nid],id, bitPos + 4,t);
        if (bitPos >= 27){
            a[nid] = t;
        }else {
            Object[] oa = (Object[]) a[nid];
            if (oa != null) {
                boolean rem = false;
                for (int i = 0; i < 16; i++) {
                    if (rem = (oa[i] != null)) break;
                }
                if (!rem) {
                    a[nid] = null;
                }
            }
        }
        return r;
    }
    public static <T> T nodesNodeRemove_B4(Object[] a,int id,int bitPos){
        int nid = (id >> bitPos) & 0xf;
        if (a[nid] == null) {
            return null;
        }
        T r = bitPos >= 27 ? (T)a[nid] : nodesNodeRemove_B4((Object[]) a[nid],id, bitPos + 4);
        if (bitPos >= 27){
            a[nid] = null;
        }else {
            Object[] oa = (Object[]) a[nid];
            if (oa != null) {
                boolean rem = false;
                for (int i = 0; i < 16; i++) {
                    if (rem = (oa[i] != null)) break;
                }
                if (!rem) {
                    a[nid] = null;
                }
            }
        }
        return r;
    }

    @Override
    public int size() {
        return entrySet.size();
    }

    @Override
    public boolean isEmpty() {
        return entrySet.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return nodesNodeGetter_B4(rootNodes,key == null ? 0 : key.hashCode(),0) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return nodesNodeGetter_B4(rootNodes,value == null ? 0 : value.hashCode(),0) != null;
    }

    @Override
    public V get(Object key) {
        Node<K,V> n = nodesNodeGetter_B4(rootNodes,key == null ? 0 : key.hashCode(),0);
        return n == null ? null : n.getValue();
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        synchronized (this){
            int kh = key == null ? 0 : key.hashCode();
            int vh = value == null ? 0 : value.hashCode();
            Node<K, V> node = value == null ? null : new Node<>(entrySet.size()-1, key, value);

            entrySet.add(node);
            nodesNodeSetter_B4(rootNodeValues, vh, 0, node);
            Node<K, V> node1 = nodesNodeSetter_B4(rootNodes, kh, 0, node);
            return node1 == null ? null : node1.value;
        }
    }

    public  Node removeNode(Node key) {
        for (int i = 0; i < entrySet.size(); i++) {
            if (entrySet.get(i) == key) {
                entrySet.remove(i);
                return key;
            }
        }
        return key;
    }

    @Override
    public synchronized V remove(Object key) {
        synchronized (this) {
            Node<K,V> node = nodesNodeRemove_B4(rootNodes, key == null ? 0 : key.hashCode(), 0);
            Entry<K,V> remove = node != null ? removeNode(node) : null;
            nodesNodeRemove_B4(rootNodeValues, node == null || node.getValue() == null ? 0 : node.getValue().hashCode(), 0);
            return remove == null ? null : remove.getValue();
        }
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        rootNodes = new Object[16];
        rootNodeValues = new Object[16];
        entrySet.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        HashSet<K> r = new HashSet<>();
        for (Entry<K, V> kvEntry : entrySet()) {
            r.add(kvEntry.getKey());
        }
        return r;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new MapEntrySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        ArrayList<V> r = new ArrayList<>();
        for (Entry<K, V> kvEntry : entrySet()) {
            r.add(kvEntry.getValue());
        }
        return r;
    }

    static class Node<K,V> implements Map.Entry<K,V> {
        final int pos;
        final K key;
        V value;

        Node(int pos, K key, V value) {
            this.pos = Math.max(pos, 0);
            this.key = key;
            this.value = value;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

    private final class MapEntrySet implements Set<Entry<K, V>> {
        private static final int SPLITERATOR_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.ORDERED;

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return entrySet.iterator();
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return entrySet.toArray();
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return entrySet.toArray(a);
        }

        @Override
        public boolean add(Entry<K, V> kvEntry) {
            NodeHashMap.this.put(kvEntry.getKey(), kvEntry.getValue());
            return true;
        }

        @Override
        public Spliterator<Entry<K, V>> spliterator() {
            return entrySet.spliterator();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(final Object o) {
            return entrySet.contains(o);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return entrySet.containsAll(c);
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends Entry<K, V>> c) {
            for (Entry<K, V> o : c) {
                NodeHashMap.this.put(o.getKey(), o.getValue());
            }
            return true;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            for (Object o : c) {
                NodeHashMap.this.remove(o);
            }
            return true;
        }

        @Override
        public int size() {
            return entrySet.size();
        }

        @Override
        public boolean isEmpty() {
            return NodeHashMap.this.isEmpty();
        }

        @Override
        public void clear() {
            NodeHashMap.this.clear();
        }

    }

}
