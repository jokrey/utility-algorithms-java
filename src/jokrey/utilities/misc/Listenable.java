package jokrey.utilities.misc;

import java.util.LinkedList;

/**
 * Implements the observer pattern with generics
 * Not a lot more than a thread safe linked list wrapper with an auto iterator
 */
public class Listenable<E> {
    private final LinkedList<Listener<E>> listeners = new LinkedList<>();
    public void notifyMe(Listener<E> listener) {
        add(listener);
    }
    public synchronized void add(Listener<E> listener) {
        listeners.add(listener);
    }
    public synchronized void remove(Listener<E> listener) {
        listeners.remove(listener);
    }

    public synchronized void notify(E e) {
        for(Listener<E> listener : listeners)
            listener.notified(e);
    }
}