package n1luik.K_multi_threading.core.Imixin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface IWorldChunkLockedConfig {
    void pushThread(long id);
    long pushThread();
    void pushWaitThread(long id);
    long pushWaitThread();
    void pop(long id);
    void pop();
    void popWait(long id);
    void popWait();
    List<Thread> getGeneratorThread1();
    Thread getGeneratorThread2();
    void execTasks();
    void execTask(Runnable task);
    void execWaitTask(Runnable task);
    void KMT$addTickRun(Runnable task);
    void KMT$addRun(Runnable task);
    boolean isGeneratorWait();
}
