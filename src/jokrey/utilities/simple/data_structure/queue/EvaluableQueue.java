package jokrey.utilities.simple.data_structure.queue;

public interface EvaluableQueue<E> extends Queue<E> {
    void println();
    void enqueue(E e, long sleep_at_some_point);
    E dequeue(long sleep_at_some_point);
    E peek(long sleep_at_some_point);
}
