package n1luik.K_multi_threading.core.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GetterSyncNode<V, K, T> extends ArrayList<GetterSyncNode<?,?,?>> implements Function<T, V> {
    protected volatile GetterSyncNode<?, ?, ?> syncRun;
    protected Sync<GetterSyncNode<V, K, T>> sync;
    protected runFun<Function<K, GetterSyncNode<?, ?, ?>>, T, V> run;
    protected Function<K, GetterSyncNode<?, ?, ?>> map;
    protected Node<runFun<Function<K, GetterSyncNode<?, ?, ?>>, T, V>> node;
    public final int hash = Long.hashCode(System.nanoTime() ^ System.currentTimeMillis());//防止哈希一样

    public GetterSyncNode(
            runFun<Function<K, GetterSyncNode<?, ?, ?>>, T, V> run,
            Function<K, GetterSyncNode<?, ?, ?>> map,
            Node<runFun<Function<K, GetterSyncNode<?, ?, ?>>, T, V>> n
    ) {
        this.run = run;
        this.map = map;
        this.node = new Node<>(run,n);
    }

    public void setSync(GetterSyncNode s){
        add(s);
        syncRun = s;
    }

    public void setBase(Sync<GetterSyncNode<V, K, T>> s){
        sync = s;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public V apply(T t) {
        return run.apply(this,this::syncData, t);
    }

    protected GetterSyncNode<?, ?, ?> syncData(K key) {
        GetterSyncNode<?, ?, ?> apply = this.map.apply(key);
        apply.wait(this);
        apply.add(this);
        return apply;
    }

    public void wait(GetterSyncNode s){
        if (!node.contains(s.run) && s.syncRun != this) {
            synchronized (s) {
                while (s.syncRun != null && !node.contains(s.syncRun)) Thread.onSpinWait();
                s.syncRun = this;
            }
            setSync(s);
        }//判断是不是同进程组
    }

    @Override
    public void clear() {
        for (GetterSyncNode<?, ?, ?> getterSyncNode : this) {
            getterSyncNode.syncRun = null;
        }
        super.clear();
    }

    public static class Node<V> {
        V value;
        Node<V> next;
        public final int hash = Long.hashCode(System.nanoTime() ^ System.currentTimeMillis());//防止哈希一样

        Node(V value, Node<V> next) {
            this.value = value;
            this.next = next;
        }

        public V getValue()      { return value; }

        public int hashCode() {
            return hash;
        }

        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Map.Entry<?, ?> e && Objects.equals(value, e.getValue());
        }

        public boolean contains(Object o) {
            return o == null || value == o || (next != null && next.contains(o));
        }

    }

    public static interface runFun<T, U, R> {
        R apply(GetterSyncNode<?, ?, ?> base, T t, U u);
    }
}
