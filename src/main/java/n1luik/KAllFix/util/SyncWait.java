package n1luik.KAllFix.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SyncWait {
    private volatile int level = 0;
    protected final Object levelLock = new Object();
    protected Thread thread = null;


    public void pop() {
        synchronized (levelLock) {
            level--;
            if (level == 0){
                thread = null;
            }else if (level < 0) {
                throw new IllegalStateException("Tried to pop past 0");
            }
        }
    }
    public void push() {
        synchronized (levelLock) {
            level++;
            if (thread != null && thread != Thread.currentThread()) {
                waitRun();
            }
            thread = Thread.currentThread();
        }
    }

    public synchronized void waitRun(){
        if (thread != null && thread != Thread.currentThread()) {
            while (level > 0) {
                Thread.onSpinWait();
            }
        }
    }

}
