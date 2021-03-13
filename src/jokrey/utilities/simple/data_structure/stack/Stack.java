package jokrey.utilities.simple.data_structure.stack;

/**
 * @author jokrey
 */
public interface Stack<E> {
    void push(E e);
    E pop();
    E top();
    int size();
    default boolean isEmpty() {return size()==0;}
    default E head() {return top();}

    void clear();
}
