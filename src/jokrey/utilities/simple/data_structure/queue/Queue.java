package jokrey.utilities.simple.data_structure.queue;

/**
 * @author jokrey
 */
public interface Queue<E> {
    /**Add given element to queue, must not be null*/
    void enqueue(E e);
    /**Return and Remove next elem in queue or null*/
    E dequeue();
    /**Return next elem in queue or null*/
    E peek();
    /**Calculate the size, might be done by iteration (can be costly)*/
    int size();
    /**Clears the contents*/
    void clear();


    //debug or test functionality:
    default void println() {throw new UnsupportedOperationException();}
    default void enqueue(E e, long sleep_at_some_point) {throw new UnsupportedOperationException();}
    default E dequeue(long sleep_at_some_point) {throw new UnsupportedOperationException();}
    default E peek(long sleep_at_some_point) {throw new UnsupportedOperationException();}
}
