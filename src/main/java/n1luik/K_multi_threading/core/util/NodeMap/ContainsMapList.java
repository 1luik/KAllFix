package n1luik.K_multi_threading.core.util.NodeMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import static it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;

public class ContainsMapList<V> implements List<V> {

    public ContainsMapList(boolean enableEntrySet){
        this.enableEntrySet = enableEntrySet;
        entrySet = new ObjectArrayList<>();
    }
    public ContainsMapList(){
        this(true);
        entrySet = new ObjectArrayList<>();
    }

    public ContainsMapList(int l){
        this(true);
        entrySet = new ObjectArrayList<>(l);
    }

    @Override
    public boolean contains(Object o) {
        return containsValue(o);
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return values().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return values().toArray(a);
    }

    @Override
    public boolean add(V v) {
        add(entrySet.size(),v);
        return true;
    }

    @Override
    public V set(int index, V element) {
        return put(index,element);
    }

    @Override
    public void add(int index, V element) {
        put(index,element);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!containsValue(o))return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        for (V v : c) {
            add(v);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends V> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        Entry<V> integer = nodesNodeGetter_B4(rootNodes, o == null ? 0 : o.hashCode(), 0);
        return integer.getIntKey();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<V> listIterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<V> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    public final boolean enableEntrySet;
    protected ObjectArrayList<Entry<V>> entrySet;
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

    public boolean containsKey(int key) {
        return nodesNodeGetter_B4(rootNodes,Integer.hashCode(key),0) != null;
    }

    public boolean containsValue(Object value) {
        return nodesNodeGetter_B4(rootNodes,value == null ? 0 : value.hashCode(),0) != null;
    }

    @Override
    public V get(int key) {
        Node<V> n = nodesNodeGetter_B4(rootNodes,Integer.hashCode(key),0);
        return n == null ? null : n.getValue();
    }

    @Nullable
    public V put(int key, V value) {
        int kh = Integer.hashCode(key);
        int vh = value == null ? 0 : value.hashCode();
        Node<V> node = new Node<>(entrySet.size()-1, key, value);
        if (enableEntrySet){
            synchronized (this) {
                entrySet.add(node);
            }
        }
        nodesNodeSetter_B4(rootNodeValues, vh, 0, node);
        Node<V> node1 = nodesNodeSetter_B4(rootNodes, kh, 0, node);
        return node1 == null ? null : node1.value;
    }

    public  Node removeNode(Node key) {
        for (int i = 0; i < entrySet.size(); i++) {
            if (entrySet.get(i) == key) {
                if (enableEntrySet) {
                    synchronized (this) {
                        entrySet.remove(i);
                    }
                }
                return key;
            }
        }
        return key;
    }

    @Override
    public boolean remove(Object key) {
        Node<V> node = nodesNodeRemove_B4(rootNodes, key == null ? 0 : key.hashCode(), 0);
        Entry<V> remove = node != null ? removeNode(node) : null;
        nodesNodeRemove_B4(rootNodeValues, node == null && node.getValue() == null ? 0 : node.getValue().hashCode(), 0);
        return true;
    }

    @Override
    public V remove(int key) {
        Node<V> node = nodesNodeRemove_B4(rootNodes, Integer.hashCode(key), 0);
        Entry<V> remove = node != null ? removeNode(node) : null;
        nodesNodeRemove_B4(rootNodeValues, node == null || node.getValue() == null ? 0 : node.getValue().hashCode(), 0);
        return remove == null ? null : remove.getValue();
    }

    @NotNull
    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public void putAll(@NotNull Int2ObjectMap<? extends V> m) {
        for (Entry<? extends V> entry : m.int2ObjectEntrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        rootNodes = new Object[16];
        rootNodeValues = new Object[16];
        entrySet.clear();
    }

    public ObjectSet<Entry<V>> int2ObjectEntrySet() {
        return ObjectSet.of(entrySet.toArray(Entry[]::new));
    }

    @NotNull
    public IntSet keySet() {
        IntSet r = new IntArraySet();
        for (Entry<V> kvEntry : entrySet) {
            r.add(kvEntry.getKey());
        }
        return r;
    }

    @NotNull
    public ObjectSet<Map.Entry<Integer, V>> entrySet() {
        return ObjectSet.of(entrySet.toArray(new Entry[0]));
    }

    @NotNull
    public ObjectCollection<V> values() {
        ObjectArrayList<V> r = new ObjectArrayList<>();
        for (Entry<V> kvEntry : entrySet) {
            r.add(kvEntry.getValue());
        }
        return r;
    }

    static class Node<V> implements Entry<V> {
        final int pos;
        final int key;
        V value;

        Node(int pos, int key, V value) {
            this.pos = Math.max(pos, 0);
            this.key = key;
            this.value = value;
        }

        public final int getIntKey()        { return key; }
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


}
