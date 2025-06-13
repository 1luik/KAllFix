package n1luik.K_multi_threading.debug.ex;

import lombok.Getter;
import n1luik.K_multi_threading.core.util.Unsafe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DebugVoidAsyncWait implements Runnable{
    protected static final long err_pos;
    protected static final long status_pos;
    static {

        try {
            err_pos = Unsafe.unsafe.objectFieldOffset(DebugVoidAsyncWait.class.getDeclaredField("err"));
            status_pos = Unsafe.unsafe.objectFieldOffset(DebugVoidAsyncWait.class.getDeclaredField("status"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }
    public final Lock lock;
    public final Condition condition;
    public final Runnable task;
    @Getter
    protected volatile Throwable err = null;
    protected volatile int status = 0;


    public Throwable getError_() {
        return (Throwable) Unsafe.unsafe.getObjectVolatile(this, err_pos);
    }

    public int getStatus() {
        return Unsafe.unsafe.getIntVolatile(this, status_pos);
    }



    public void setErr_(Throwable v) {
        Unsafe.unsafe.putObjectVolatile(this, err_pos, v);
    }

    protected void setStatus_(int v) {
        Unsafe.unsafe.putIntVolatile(this, status_pos, v);
    }


    public DebugVoidAsyncWait(Lock lock, Condition condition, Runnable task) {
        this.lock = lock;
        this.condition = condition;
        this.task = task;
    }

    public DebugVoidAsyncWait(Runnable task) {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.task = task;
    }

    @Override
    public void run() {
        try {
            task.run();
        }catch (Throwable e){
            setErr_(e);
            lock.lock();
            try {
                synchronized (this){
                    setStatus_(2);
                    condition.signal(); // 异步任务完成时发送信号
                }
            } finally {
                lock.unlock();
            }
            return;
        }
        //因为lootr 会返回null所以不能进行安全检查
        lock.lock();
        try {
            synchronized (this){
                setStatus_(2);
                condition.signal(); // 异步任务完成时发送信号
            }
        } finally {
            lock.unlock();
        }

    }
    public void waitTask(){
        if (status == 4) throw new RuntimeException("任务已经完成");
        lock.lock();
        try {
            synchronized (this){
                if (getStatus() == 0) {
                    setStatus_(1);
                }
            }
            while (getStatus() < 2) {
                condition.await(10, TimeUnit.MILLISECONDS); // 带超时的等待
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            status = 4;
        }
    }
}
