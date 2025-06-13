package n1luik.K_multi_threading.core.util.NodeMap;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class Long2ObjectLinkedOpenHashMapLong2ObjectNodeHashMap<V> extends Long2ObjectLinkedOpenHashMap<V> {
    protected ObjectArrayList<Entry<V>> entrySet = new ObjectArrayList<>();
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
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        return nodesNodeGetter_B4(rootNodes,Long.hashCode(key),0) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return nodesNodeGetter_B4(rootNodes,value == null ? 0 : value.hashCode(),0) != null;
    }

    @Override
    public V get(Object key) {
        Node<V> n = nodesNodeGetter_B4(rootNodes,key == null ? 0 : key.hashCode(),0);
        return n == null ? null : n.getValue();
    }

    @Nullable
    @Override
    public V put(long key, V value) {
        //synchronized (this){
            int kh = Long.hashCode(key);
            int vh = value == null ? 0 : value.hashCode();
            Node<V> node = new Node<>(entrySet.size()-1, key, value);

            entrySet.add(node);
            nodesNodeSetter_B4(rootNodeValues, vh, 0, node);
            Node<V> node1 = nodesNodeSetter_B4(rootNodes, kh, 0, node);
            return node1 == null ? null : node1.value;
        //}
    }

    @Override
    public V get(long key) {
        Node<V> n = nodesNodeGetter_B4(rootNodes,Long.hashCode(key),0);
        return n == null ? null : n.getValue();
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
    public V remove(Object key) {
        //synchronized (this){
            Node<V> node = nodesNodeRemove_B4(rootNodes, key == null ? 0 : key.hashCode(), 0);
            Entry<V> remove = node != null ? removeNode(node) : null;
            nodesNodeRemove_B4(rootNodeValues, node == null || node.getValue() == null ? 0 : node.getValue().hashCode(), 0);
            return remove == null ? null : remove.getValue();
        //}
    }

    @Override
    public V remove(long key) {
        //synchronized (this){
            Node<V> node = nodesNodeRemove_B4(rootNodes, Long.hashCode(key), 0);
            Entry<V> remove = node != null ? removeNode(node) : null;
            nodesNodeRemove_B4(rootNodeValues, node == null || node.getValue() == null ? 0 : node.getValue().hashCode(), 0);
            return remove == null ? null : remove.getValue();
        //}
    }

    @Override
    public void putAll(@NotNull Map<? extends Long, ? extends V> m) {
        for (Map.Entry<? extends Long, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());

        }

    }

    public void putAll(@NotNull Long2ObjectMap<? extends V> m) {
        for (Entry<? extends V> entry : m.long2ObjectEntrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        rootNodes = new Object[16];
        rootNodeValues = new Object[16];
        entrySet.clear();
        size = 0;
    }

    protected V defRetValue;

    @Override
    public void defaultReturnValue(final V rv) {
        defRetValue = rv;
    }

    @Override
    public V defaultReturnValue() {
        return defRetValue;
    }

    @Override
    public FastSortedEntrySet<V> long2ObjectEntrySet() {
        return new MapEntrySet();
    }

    @Override
    public LongSortedSet keySet() {
        LongLinkedOpenHashSet r = new LongLinkedOpenHashSet();
        for (Entry<V> kvEntry : entrySet) {
            r.add(kvEntry.getKey());
        }
        return r;
    }

    //@Override
    //public ObjectSortedSet<Map.Entry<Long, V>> entrySet() {
    //    return ObjectLinkedOpenHashSet.of(entrySet.toArray(new Entry[0]));
    //}

    @NotNull
    @Override
    public ObjectCollection<V> values() {
        ObjectArrayList<V> r = new ObjectArrayList<>();
        for (Entry<V> kvEntry : entrySet) {
            r.add(kvEntry.getValue());
        }
        return r;
    }

    static class Node<V> implements Entry<V> {
        final int pos;
        final long key;
        V value;

        Node(int pos, long key, V value) {
            this.pos = Math.max(pos, 0);
            this.key = key;
            this.value = value;
        }

        public final long getLongKey()        { return key; }
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

            return o instanceof Entry<?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

    private final class MapEntrySet extends AbstractObjectSortedSet<Long2ObjectMap.Entry<V>> implements FastSortedEntrySet<V> {
        private static final int SPLITERATOR_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.ORDERED;

        @Override
        public ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> iterator() {
            return entrySet.iterator();
        }

        @Override
        public ObjectSpliterator<Long2ObjectMap.Entry<V>> spliterator() {
            return entrySet.spliterator();
        }

        @Override
        public Comparator<? super Entry<V>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Long2ObjectMap.Entry<V>> subSet(Long2ObjectMap.Entry<V> fromElement, Long2ObjectMap.Entry<V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Long2ObjectMap.Entry<V>> headSet(Long2ObjectMap.Entry<V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Long2ObjectMap.Entry<V>> tailSet(Long2ObjectMap.Entry<V> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long2ObjectMap.Entry<V> first() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long2ObjectMap.Entry<V> last() {
            throw new UnsupportedOperationException();
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
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            Long2ObjectLinkedOpenHashMapLong2ObjectNodeHashMap.this.clear();
        }

        /**
         * Returns a type-specific list iterator on the elements in this set, starting from a given element
         * of the set. Please see the class documentation for implementation details.
         *
         * @param from an element to start from.
         * @return a type-specific list iterator starting at the given element.
         * @throws IllegalArgumentException if {@code from} does not belong to the set.
         */
        @Override
        public ObjectListIterator<Long2ObjectMap.Entry<V>> iterator(final Long2ObjectMap.Entry<V> from) {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns a type-specific fast list iterator on the elements in this set, starting from the first
         * element. Please see the class documentation for implementation details.
         *
         * @return a type-specific list iterator starting at the first element.
         */
        @Override
        public ObjectListIterator<Long2ObjectMap.Entry<V>> fastIterator() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns a type-specific fast list iterator on the elements in this set, starting from a given
         * element of the set. Please see the class documentation for implementation details.
         *
         * @param from an element to start from.
         * @return a type-specific list iterator starting at the given element.
         * @throws IllegalArgumentException if {@code from} does not belong to the set.
         */
        @Override
        public ObjectListIterator<Long2ObjectMap.Entry<V>> fastIterator(final Long2ObjectMap.Entry<V> from) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        @Override
        public void forEach(final Consumer<? super Entry<V>> consumer) {
            entrySet.forEach(consumer);
        }

        /** {@inheritDoc} */
        @Override
        public void fastForEach(final Consumer<? super Long2ObjectMap.Entry<V>> consumer) {
            throw new UnsupportedOperationException();
        }
    }

}
