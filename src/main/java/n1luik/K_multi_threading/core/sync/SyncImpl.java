package n1luik.K_multi_threading.core.sync;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import n1luik.K_multi_threading.core.Base;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

@Deprecated
public class SyncImpl<T extends GetterSyncNode<?,?,?>> implements Sync<T>{
    public final ForkJoinPool forkJoinPool;
    public SyncImpl(ForkJoinPool forkJoinPool){
        this.forkJoinPool = forkJoinPool;
    }
    public final List<T> syncs = new ObjectArrayList<>(){
        @Override
        public synchronized boolean add(T getterSyncNodes) {
            return super.add(getterSyncNodes);
        }

        @Override
        public synchronized T remove(int index) {
            return super.remove(index);
        }
    };



    @Override
    public <V> V run(Function<T, V> r, T t) {

        return CompletableFuture.supplyAsync(() -> {
            syncs.add(t);
            t.setBase((SyncImpl)this);
            V apply = r.apply(t);
            t.clear();
            syncs.remove(t);
            return apply;
        }, forkJoinPool).join();
    }

    @Override
    public <V> V _run(Function<T, V> r, T t, Base.ForkJoinWorkerThread_ thread) {//慢
        //int key = (thread.hashCode());
        //Object buf = thread.getDataMap().get(key);
        //thread.getDataMap().put(key,t);
        //syncs.add(t);
        //t.setBase((SyncImpl)this);
        //V apply = r.apply(t);
        //t.clear();
        //syncs.remove(t);
        //if(buf != null) thread.getDataMap().put(key,buf);else thread.getDataMap().remove(key);
        return r.apply(t);//apply;
    }

    @Override
    public <V> V _run(Function<T, V> r, T t, Base.ForkJoinPool_ thread) {
        int key = (thread.hashCode());
        Object buf = thread.getDataMap().get(key);
        thread.getDataMap().put(key,t);
        syncs.add(t);
        t.setBase((SyncImpl)this);
        V apply = r.apply(t);
        t.clear();
        syncs.remove(t);
        if(buf != null) thread.getDataMap().put(key,buf);else thread.getDataMap().remove(key);
        return apply;
    }

    @Override
    public <V> CompletableFuture<V> runTask(Function<T, V> r, T t) {
        return CompletableFuture.supplyAsync(() -> {
            syncs.add(t);
            t.setBase((SyncImpl)this);
            V apply = r.apply(t);
            t.clear();
            syncs.remove(t);
            return apply;
        }, forkJoinPool);
    }

    @Override
    public boolean isDisable(T t) {
        return false;
    }

    @Override
    public void disable(T t) {

    }

    @Override
    public int size() {
        return syncs.size();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return syncs.iterator();
    }
}
