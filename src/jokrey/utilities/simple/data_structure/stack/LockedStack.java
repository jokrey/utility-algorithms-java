package jokrey.utilities.simple.data_structure.stack;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jokrey
 */
public class LockedStack<E> extends LinkedStack<E> {
    private final Lock lock = new ReentrantLock();

    @Override public void push(E e) {
        lock.lock();
        try {
            super.push(e);
        } finally {lock.unlock();}
    }

    @Override public E pop() {
        lock.lock();
        try {
            return super.pop();
        } finally {lock.unlock();}
    }


    @Override public int size() {
        lock.lock();
        try {
            return super.size();
        } finally {lock.unlock();}
    }

    @Override public E peek() {
        lock.lock();
        try {
            return super.peek();
        } finally {lock.unlock();}
    }



    @Override public void push(E e, long sleep_at_some_point) {
        lock.lock();
        try {
            super.push(e, sleep_at_some_point);
        } finally {lock.unlock();}
    }

    @Override public E pop(long sleep_at_some_point) {
        lock.lock();
        try {
            return super.pop(sleep_at_some_point);
        } finally {lock.unlock();}
    }

    @Override public E peek(long sleep_at_some_point) {
        lock.lock();
        try {
            return super.peek(sleep_at_some_point);
        } finally {lock.unlock();}
    }
}
