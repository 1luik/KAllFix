package n1luik.K_multi_threading.core.util.concurrent;

import java.util.ArrayList;

public class LockArrayList<T> extends ArrayList<T>{
    @Override
    public synchronized boolean add(T t) {
        return super.add(t);
    }

    @Override
    public synchronized Object clone() {
        return super.clone();
    }

    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }
}
