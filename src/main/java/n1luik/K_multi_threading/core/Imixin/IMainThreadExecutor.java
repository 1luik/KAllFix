package n1luik.K_multi_threading.core.Imixin;

public interface IMainThreadExecutor {
    void k_multi_threading$pushThread();
    boolean k_multi_threading$notCallPollTask();
    boolean isCall();
    void setM2(boolean m2);
    void k_multi_threading$setMultiThreading(int size);
    Thread getCallThread();
    Object getLockCall();
}
