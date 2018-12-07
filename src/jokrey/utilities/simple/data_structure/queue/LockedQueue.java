package jokrey.utilities.simple.data_structure.queue;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jokrey
 */
public class LockedQueue<E> extends LinkedQueue<E> {
    private final Lock lock = new ReentrantLock();

    public void enqueue(E e) {
        lock.lock();
        try {
            super.enqueue(e);
        } finally {
            lock.unlock();
        }
    }

    public E dequeue() {
        lock.lock();
        try {
            return super.dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override public E peek() {
//        return first==null ? null : first.val; // not possible: first can become null, between checks.
        lock.lock();
        try {
            return super.peek();
        } finally {
            lock.unlock();
        }
    }

    @Override public int size() {
        lock.lock();
        try {
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override public void enqueue(E e, long sleep_at_some_point) {
        lock.lock();
        try {
            super.enqueue(e, sleep_at_some_point);
        } finally {
            lock.unlock();
        }
    }

    @Override public E dequeue(long sleep_at_some_point) {
        lock.lock();
        try {
            return super.dequeue(sleep_at_some_point);
        } finally {
            lock.unlock();
        }
    }

    @Override public E peek(long sleep_at_some_point) {
        lock.lock();
        try {
            return super.peek(sleep_at_some_point);
        } finally {
            lock.unlock();
        }
    }
}