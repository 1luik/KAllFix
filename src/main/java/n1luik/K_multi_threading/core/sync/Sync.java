package n1luik.K_multi_threading.core.sync;

import n1luik.K_multi_threading.core.Base;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.function.Function;

public interface Sync<T extends GetterSyncNode> extends Iterable<T> {
    <V> V run(Function<T,V> r, T t);
    <V> V _run(Function<T, V> r, T t, Base.ForkJoinWorkerThread_ thread);
    <V> V _run(Function<T, V> r, T t, Base.ForkJoinPool_ thread);
    <V> CompletableFuture<V> runTask(Function<T,V> r, T t);
    boolean isDisable(T t);
    void disable(T t);

    int size();
}
