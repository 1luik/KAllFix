package n1luik.K_multi_threading.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//如果sync是this任务交流线程其他添加会阻塞
public class AbstractWaitImpl<T> implements WaitCall<T>, Iterable<Thread> {
    protected volatile boolean disable = false;
    protected final List<Thread> wait = new ArrayList<>();
    protected final List<Runnable> calls = new ArrayList<>();

    @Override
    public void wait(T t) {
        synchronized(calls){
            if (disable)return;
            wait.add(Thread.currentThread());
        }
        Unsafe.unsafe.park(false,0);
    }

    public void unpark(){

        synchronized(calls){
            disable = true;

            while (calls.size() > 0) {
                calls.remove(calls.size()-1).run();
            }

            while (wait.size() > 0) {
                Unsafe.unsafe.unpark(wait.remove(wait.size()-1));
            }


            disable = false;
        }
    }

    @NotNull
    @Override
    public Iterator<Thread> iterator() {
        return wait.iterator();
    }

    public void setDisable(boolean disable) {
        synchronized (calls) {
            this.disable = disable;
        }
    }

    @Override
    public void executeTask(@NotNull Runnable command) {
        synchronized (calls){
            calls.add(command);
        }
    }
}
