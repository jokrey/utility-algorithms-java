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
}
