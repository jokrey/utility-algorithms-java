package jokrey.utilities.simple.data_structure.stack;

public interface EvaluableStack<E> extends Stack<E> {
    //debug or test functionality:
    default void println() {throw new UnsupportedOperationException();}
    default void push(E e, long sleep_at_some_point) {throw new UnsupportedOperationException();}
    default E pop(long sleep_at_some_point) {throw new UnsupportedOperationException();}
    default E peek(long sleep_at_some_point) {throw new UnsupportedOperationException();}
}
