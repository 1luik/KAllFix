package n1luik.KAllFix.util;

import com.mojang.logging.LogUtils;
import n1luik.K_multi_threading.core.util.Unsafe;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 警告线程需要手动启动
 * */
public class TaskRun implements Executor {
    static Logger LOGGER = LogUtils.getLogger();
    public final Thread TaskRun;
    protected volatile boolean isRunning = false;
    protected volatile boolean stop = false;
    protected final Object StateLock = new Object();
    private final ReentrantLock isPark = new ReentrantLock();
    protected final ConcurrentLinkedQueue<Runnable> Tasks = new ConcurrentLinkedQueue<>();
    public TaskRun(String name) {
        this(name, () -> {});
    }
    public TaskRun(String name, Runnable start) {
        TaskRun = new Thread(name){
            @Override
            public void run(){
                start.run();
                while (true) {
                    synchronized (StateLock) {
                        isRunning = true;
                    }
                    while (!Tasks.isEmpty()){
                        try{
                            Tasks.remove().run();
                        }catch (Throwable e){
                            LOGGER.error("Error while running task", e);
                        }
                    }
                    synchronized (StateLock) {
                        if (!Tasks.isEmpty())continue;
                        isRunning = false;
                    }
                    isPark.lock();
                    if (!Tasks.isEmpty())continue;
                    Unsafe.unsafe.park(false, 0L);
                    isPark.unlock();
                    if (stop)break;
                }
            }
        };
    }

    @Override
    public void execute(@NotNull Runnable command) {
        synchronized (StateLock) {
            Tasks.add(command);
            if (isPark.isLocked()) {
                Unsafe.unsafe.unpark(TaskRun);
            }else if (isPark.tryLock()) {
                isPark.unlock();
            }else {
                Unsafe.unsafe.unpark(TaskRun);
            }
        }
    }

    public void start() {
        TaskRun.start();
    }
}
