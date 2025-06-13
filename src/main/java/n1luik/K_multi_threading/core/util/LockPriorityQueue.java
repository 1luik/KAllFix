package n1luik.K_multi_threading.core.util;

import java.util.PriorityQueue;

public class LockPriorityQueue<T> extends PriorityQueue<T> {
    @Override
    public synchronized boolean add(T t) {
        return super.add(t);
    }

    @Override
    public synchronized boolean offer(T t) {
        return super.offer(t);
    }

    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public synchronized T poll() {
        return super.poll();
    }

    @Override
    public synchronized T element() {
        return super.element();
    }

    @Override
    public synchronized T peek() {
        return super.peek();
    }
}

