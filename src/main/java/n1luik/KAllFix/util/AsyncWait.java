package n1luik.KAllFix.util;

import lombok.Getter;
import n1luik.K_multi_threading.core.util.Unsafe;
import n1luik.K_multi_threading.core.util.VOB3_OOI;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class AsyncWait<T> implements Runnable{
    protected static final long err_pos;
    protected static final long ret_pos;
    protected static final long status_pos;
    static {

        try {
            err_pos = Unsafe.unsafe.objectFieldOffset(AsyncWait.class.getDeclaredField("err"));
            ret_pos = Unsafe.unsafe.objectFieldOffset(AsyncWait.class.getDeclaredField("ret"));
            status_pos = Unsafe.unsafe.objectFieldOffset(AsyncWait.class.getDeclaredField("status"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }
    public final Lock lock;
    public final Condition condition;
    public final Supplier<T> task;
    @Getter
    protected volatile T ret;
    @Getter
    protected volatile Throwable err = null;
    protected volatile int status = 0;

    public T getReturn_() {
        return (T) Unsafe.unsafe.getObjectVolatile(this, ret_pos);
    }

    public Throwable getError_() {
        return (Throwable) Unsafe.unsafe.getObjectVolatile(this, err_pos);
    }

    public int getStatus() {
        return Unsafe.unsafe.getIntVolatile(this, status_pos);
    }


    public void seRet_(T v) {
        Unsafe.unsafe.putObjectVolatile(this, ret_pos, v);
    }

    public void setErr_(Throwable v) {
        Unsafe.unsafe.putObjectVolatile(this, err_pos, v);
    }

    protected void setStatus_(int v) {
        Unsafe.unsafe.putIntVolatile(this, status_pos, v);
    }


    public AsyncWait(Lock lock, Condition condition, Supplier<T> task) {
        this.lock = lock;
        this.condition = condition;
        this.task = task;
    }
    public AsyncWait(Supplier<T> task) {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.task = task;
    }

    @Override
    public void run() {
        T r;
        try {
            r = task.get();
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
        seRet_(r);
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
    public void waitTask(Runnable wait){
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
                wait.run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            status = 4;
        }
    }
}
