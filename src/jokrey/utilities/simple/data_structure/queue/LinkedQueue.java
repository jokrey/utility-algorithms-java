package jokrey.utilities.simple.data_structure.queue;

import jokrey.utilities.simple.data_structure.pairs.Node;

import static jokrey.utilities.simple.data_structure.queue.ConcurrentQueueTest.sleep;

/**
 * @author jokrey
 */
public class LinkedQueue<E> implements Queue<E> {
    private Node<E> first = null;
    private Node<E> last = null;

    public void enqueue(E e) {
        final Node<E> oldLast = last;
        final Node<E> newLast = new Node<>(e, null);
        last = newLast;
        if (oldLast == null)
            first = newLast;
        else
            oldLast.next = newLast;
    }

    public E dequeue() {
        if(first==null) //queue empty
            return null;

        E val = first.val;

        if(first == last) {
            first = null;
            last = null;
        } else {
            first=first.next;
        }

        return val;
    }

    @Override public E peek() {
        return first==null ? null : first.val;
    }

    @Override public int size() {
        int counter = 0;
        Node<E> current = first;
        while(current!=null) {
            current=current.next;
            counter++;
        }
        return counter;
    }

    public void println() {
        System.out.println(first !=null ? first.toString() : "null");
    }




    @Override public void enqueue(E e, long sleep_at_some_point) {
        final Node<E> oldLast = last;
        sleep(sleep_at_some_point/4);
        final Node<E> newLast = new Node<>(e, null);
        sleep(sleep_at_some_point/4);
        last = newLast;
        sleep(sleep_at_some_point/4);
        if (oldLast == null)
            first = newLast;
        else
            oldLast.next = newLast;
        sleep(sleep_at_some_point/4);
    }

    @Override public E dequeue(long sleep_at_some_point) {
        sleep(sleep_at_some_point/4);
        if(first==null) //queue empty
            return null;
        sleep(sleep_at_some_point/4);

        E val = first.val;
        sleep(sleep_at_some_point/4);

        if(first == last) {
            first = null;
            last = null;
        } else {
            first=first.next;
        }
        sleep(sleep_at_some_point/4);

        return val;
    }

    @Override public E peek(long sleep_at_some_point) {
        sleep(sleep_at_some_point);
        return first==null ? null : first.val;
    }
}