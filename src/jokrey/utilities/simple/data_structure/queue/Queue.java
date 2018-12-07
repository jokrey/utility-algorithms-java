package jokrey.utilities.simple.data_structure.queue;

/**
 * @author jokrey
 */
public interface Queue<E> {
    void enqueue(E e);
    E dequeue();
    E peek();
    int size();


    //debug or test functionality:
    void println();
    void enqueue(E e, long sleep_at_some_point);
    E dequeue(long sleep_at_some_point);
    E peek(long sleep_at_some_point);
}
