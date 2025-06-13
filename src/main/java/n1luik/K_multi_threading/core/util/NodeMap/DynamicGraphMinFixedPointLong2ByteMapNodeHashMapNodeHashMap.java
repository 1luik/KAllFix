package n1luik.K_multi_threading.core.util.NodeMap;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;


public class DynamicGraphMinFixedPointLong2ByteMapNodeHashMapNodeHashMap implements Long2ByteMap {
    protected ObjectArrayList<Entry> entrySet = new ObjectArrayList<>();
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
    public boolean containsKey(long key) {
        return nodesNodeGetter_B4(rootNodes,Long.hashCode(key),0) != null;
    }

    @Override
    public boolean containsValue(byte value) {
        return nodesNodeGetter_B4(rootNodes,Byte.hashCode(value),0) != null;
    }

    @Override
    public Byte get(Object key) {
        Node n = nodesNodeGetter_B4(rootNodes,key == null ? 0 : key.hashCode(),0);
        return n == null ? null : n.getValue();
    }

    @Override
    public byte put(long key, byte value) {
        //synchronized (this){
            int kh = Long.hashCode(key);
            int vh = Byte.hashCode(value);
            Node node = new Node(entrySet.size()-1, key, value);

            entrySet.add(node);
            nodesNodeSetter_B4(rootNodeValues, vh, 0, node);
            Node node1 = nodesNodeSetter_B4(rootNodes, kh, 0, node);
            return node1 == null ? (byte)-1 : node1.value;
        //}
    }

    @Override
    public byte get(long key) {
        Node n = nodesNodeGetter_B4(rootNodes,Long.hashCode(key),0);
        return n == null ? (byte)-1 : n.getValue();
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
    public  Byte remove(Object key) {
        //synchronized (this){
            Node node = nodesNodeRemove_B4(rootNodes, key == null ? 0 : key.hashCode(), 0);
            Entry remove = node != null ? removeNode(node) : null;
            nodesNodeRemove_B4(rootNodeValues, node == null || node.getValue() == null ? 0 : node.getValue().hashCode(), 0);
            return remove == null ? (byte)-1 : remove.getValue();
        //}
    }

    @Override
    public byte remove(long key) {
        //synchronized (this){
            Node node = nodesNodeRemove_B4(rootNodes, Long.hashCode(key), 0);
            Entry remove = node != null ? removeNode(node) : null;
            nodesNodeRemove_B4(rootNodeValues, node == null || node.getValue() == null ? 0 : node.getValue().hashCode(), 0);
            return remove == null ? (byte)-1 : remove.getValue();
        //}
    }

    @Override
    public void putAll(@NotNull Map<? extends Long, ? extends Byte> m) {
        for (Map.Entry<? extends Long, ? extends Byte> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        rootNodes = new Object[16];
        rootNodeValues = new Object[16];
        entrySet.clear();
    }

    protected byte defRetValue;

    @Override
    public void defaultReturnValue(final byte rv) {
        defRetValue = rv;
    }

    @Override
    public byte defaultReturnValue() {
        return defRetValue;
    }

    @Override
    public ObjectSet<Entry> long2ByteEntrySet() {
        return new MapEntrySet();
    }

    @Override
    public LongSortedSet keySet() {
        LongLinkedOpenHashSet r = new LongLinkedOpenHashSet();
        for (Entry kvEntry : entrySet) {
            r.add(kvEntry.getKey());
        }
        return r;
    }

    //@Override
    //public ObjectSortedSet<Map.Entry<Long, Byte>> entrySet() {
    //    ObjectLinkedOpenHashSet<Map.Entry<Long, Byte>> ret = new ObjectLinkedOpenHashSet<>();
    //    ret.addAll(entrySet);
    //    return ret;
    //}

    @NotNull
    @Override
    public ByteCollection values() {
        ByteArrayList r = new ByteArrayList();
        for (Entry kvEntry : entrySet) {
            r.add(kvEntry.getValue());
        }
        return r;
    }

    static class Node implements Entry {
        final int pos;
        final long key;
        byte value;

        Node(int pos, long key, byte value) {
            this.pos = Math.max(pos, 0);
            this.key = key;
            this.value = value;
        }

        public final long getLongKey()        { return key; }
        public final Byte getValue()      { return value; }
        public final byte getByteValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final byte setValue(byte newValue) {
            byte oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Entry e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

    private final class MapEntrySet extends AbstractObjectSortedSet<Entry> {
        private static final int SPLITERATOR_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.ORDERED;

        @Override
        public ObjectBidirectionalIterator<Entry> iterator() {
            return entrySet.iterator();
        }

        @Override
        public ObjectSpliterator<Entry> spliterator() {
            return entrySet.spliterator();
        }

        @Override
        public Comparator<? super Entry> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Entry> subSet(Entry fromElement, Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Entry> headSet(Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Entry> tailSet(Entry fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Entry first() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Entry last() {
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
            return entrySet.size();
        }

        @Override
        public void clear() {
            DynamicGraphMinFixedPointLong2ByteMapNodeHashMapNodeHashMap.this.clear();
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
        public ObjectListIterator<Entry> iterator(final Entry from) {
            throw new UnsupportedOperationException();
        }
    }

}
