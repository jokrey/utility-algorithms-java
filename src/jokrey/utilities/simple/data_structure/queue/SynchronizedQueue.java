package jokrey.utilities.simple.data_structure.queue;

/**
 * @author jokrey
 */
public class SynchronizedQueue<E> extends LinkedQueue<E> {
    @Override public synchronized void enqueue(E e) {
        super.enqueue(e);
    }
    @Override public synchronized E dequeue() {
        return super.dequeue();
    }
    @Override public synchronized E peek() {
        return super.peek();
    }
    @Override public synchronized int size() {
        return super.size();
    }
    @Override public synchronized void clear() { super.clear(); }
    @Override public synchronized void enqueue(E e, long sleep_at_some_point) {
        super.enqueue(e, sleep_at_some_point);
    }
    @Override public synchronized E dequeue(long sleep_at_some_point) {
        return super.dequeue(sleep_at_some_point);
    }
    @Override public synchronized E peek(long sleep_at_some_point) {
        return super.peek(sleep_at_some_point);
    }
}