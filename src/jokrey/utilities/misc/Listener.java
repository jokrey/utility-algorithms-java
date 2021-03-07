package jokrey.utilities.misc;

@FunctionalInterface
public interface Listener<E> {
    void notified(E e);
}
