package n1luik.K_multi_threading.core.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 一种特殊的锁，当持有锁的线程已终止时，lock()会自动将锁转移给调用线程。
 * unlock()仅允许当前持有锁的线程释放，不检查线程存活状态。
 */
public class AutoTransferLock {

    private final Sync sync = new Sync();

    /**
     * 获取锁。如果锁未被持有，或持有者线程仍然存活且是当前线程，则正常获取。
     * 如果锁被其他已终止的线程持有，则自动将锁转移给当前调用线程。
     */
    public void lock() {
        sync.acquire(1);
    }

    /**
     * 尝试获取锁。逻辑同lock()，但支持中断。
     */
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * 非阻塞尝试获取锁。
     * 如果锁未被持有，或持有者线程仍然存活且是当前线程，返回true。
     * 如果锁被其他已终止的线程持有，转移锁并返回true。
     * 否则返回false。
     */
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    /**
     * 释放锁。仅当前持有锁的线程可以调用成功。
     * 不检查线程存活状态。
     */
    public void unlock() {
        sync.release(1);
    }

    /**
     * 返回当前持有锁的线程，或未持有则返回null。
     */
    public Thread getOwner() {
        return sync.getOwner();
    }

    /**
     * 返回是否有线程持有了该锁。
     */
    public boolean isLocked() {
        return sync.getOwner() != null;
    }

    // --- Sync implementation ---

    private static final class Sync extends AbstractQueuedSynchronizer {

        private volatile Thread owner;
        private static final VarHandle OWNER;

        static {
            try {
                OWNER = MethodHandles.lookup()
                        .findVarHandle(Sync.class, "owner", Thread.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        @Override
        protected boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();

            // 1. 完全未持有 -> 直接获取
            Thread o = owner;
            if (o == null) {
                if (OWNER.compareAndSet(this, null, current)) {
                    setState(1);
                    return true;
                }
                // CAS失败说明owner已被其他线程修改，重新读取
                o = owner;
            }

            // 2. 当前线程已持有（可重入）
            if (o == current) {
                int c = getState();
                if (c >= 0x7fffffff) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(c + 1);
                return true;
            }

            // 3. 锁被其他线程持有，检查该线程是否存活
            if (o != null && !o.isAlive()) {
                // 尝试将锁转移给当前线程
                if (OWNER.compareAndSet(this, o, current)) {
                    setState(1);
                    return true;
                }
                // CAS失败说明owner已被其他线程修改，重试
                return false;
            }

            // 持有者存活且不是当前线程 -> 获取失败
            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            Thread current = Thread.currentThread();

            // 仅允许当前持有者释放
            if (current != owner) {
                throw new IllegalMonitorStateException(
                        "Current thread does not own this lock. " +
                        "Owner: " + owner + ", Current: " + current
                );
            }

            int c = getState() - releases;
            if (c == 0) {
                OWNER.setVolatile(this, (Thread) null);
            } else {
                setState(c);
            }
            return c == 0;
        }

        Thread getOwner() {
            return owner;
        }
    }
}
