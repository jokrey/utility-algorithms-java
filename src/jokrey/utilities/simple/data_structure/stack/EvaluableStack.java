package jokrey.utilities.simple.data_structure.stack;

public interface EvaluableStack<E> extends Stack<E> {
    void println();
    void push(E e, long sleep_at_some_point);
    E pop(long sleep_at_some_point);
    E peek(long sleep_at_some_point);
}
