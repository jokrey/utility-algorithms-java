package jokrey.utilities.simple.data_structure.stack;

/**
 * @author jokrey
 */
public interface Stack<E> {
    void push(E e);
    E pop();
    E peek();
    int size();
    default boolean isEmpty() {return size()==0;}

    void clear();
}
