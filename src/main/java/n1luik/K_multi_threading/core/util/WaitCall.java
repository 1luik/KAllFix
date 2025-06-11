package n1luik.K_multi_threading.core.util;

public interface WaitCall<T> extends Wait<T>/*, Executor*/ {
    void executeTask(Runnable command);
}
