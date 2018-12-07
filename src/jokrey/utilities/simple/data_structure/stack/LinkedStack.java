package jokrey.utilities.simple.data_structure.stack;

import jokrey.utilities.simple.data_structure.pairs.Node;

import static jokrey.utilities.simple.data_structure.stack.ConcurrentStackTest.sleep;

/**
 * @author jokrey
 */
public class LinkedStack<E> implements Stack<E> {
    private Node<E> head = null;

    @Override public void push(E e) {
        head = new Node<>(e, head);
    }

    @Override public E pop() {
        if(head==null)
            return null;
        else {
            E val = head.val;
            head = head.next;
            return val;
        }
    }

    @Override public E peek() {
        return head==null? null: head.val;
    }

    @Override public int size() {
        int counter = 0;
        Node<E> current = head;
        while(current!=null) {
            current=current.next;
            counter++;
        }
        return counter;
    }

    @Override public void println() {
        System.out.println(head!=null ? head.toString() : "null");
    }



    @Override public void push(E e, long sleep_at_some_point) {
        sleep(sleep_at_some_point/2);
        head = new Node<>(e, head);
        sleep(sleep_at_some_point/2);
    }

    @Override public E pop(long sleep_at_some_point) {
        sleep(sleep_at_some_point/3);
        if(head==null)
            return null;
        else {
            E val = head.val;
            sleep(sleep_at_some_point/3);
            head = head.next;
            sleep(sleep_at_some_point/3);
            return val;
        }
    }

    @Override public E peek(long sleep_at_some_point) {
        sleep(sleep_at_some_point);
        return head==null? null: head.val;
    }
}
