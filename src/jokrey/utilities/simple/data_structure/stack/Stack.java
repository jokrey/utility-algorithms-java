package jokrey.utilities.simple.data_structure.stack;

/**
 * @author jokrey
 */
public interface Stack<E> {
    void push(E e);
    E pop();
    E peek();
    int size();


    //debug or test functionality:
    void println();
    void push(E e, long sleep_at_some_point);
    E pop(long sleep_at_some_point);
    E peek(long sleep_at_some_point);
}
