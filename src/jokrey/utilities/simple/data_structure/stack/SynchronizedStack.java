package jokrey.utilities.simple.data_structure.stack;

/**
 * @author jokrey
 */
public class SynchronizedStack<E> extends LinkedStack<E> {
    @Override public synchronized void push(E e) {
        super.push(e);
    }

    @Override public synchronized E pop() {
        return super.pop();
    }

    @Override public synchronized E peek() {
        return super.peek();
    }

    @Override public synchronized int size() {
        return super.size();
    }


    @Override public synchronized void push(E e, long sleep_at_some_point) {
        super.push(e, sleep_at_some_point);
    }

    @Override public synchronized E pop(long sleep_at_some_point) {
        return super.pop(sleep_at_some_point);
    }

    @Override public synchronized E peek(long sleep_at_some_point) {
        return super.peek(sleep_at_some_point);
    }
}