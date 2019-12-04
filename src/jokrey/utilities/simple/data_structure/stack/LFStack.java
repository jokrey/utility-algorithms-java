package jokrey.utilities.simple.data_structure.stack;

import jokrey.utilities.simple.data_structure.pairs.Node;

import java.util.concurrent.atomic.AtomicReference;

import static jokrey.utilities.simple.data_structure.stack.ConcurrentStackTest.sleep;

/**
 * @author jokrey
 */
public class LFStack<E> implements Stack<E> {
    private AtomicReference<Node<E>> head = new AtomicReference<>(null);

    @Override public void push(E e) {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = head.get();
            newHead = new Node<>(e, oldHead);
        } while(!head.compareAndSet(oldHead, newHead));
    }

    @Override public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = head.get();
            if(oldHead==null)
                return null;
            else
                newHead = oldHead.next;
        } while(! head.compareAndSet(oldHead, newHead));

        return oldHead.val;
    }

    @Override public E peek() {
        Node<E> headNode = head.get();
        return headNode==null? null: headNode.val;
    }

    @Override public int size() {
        int counter = 0;
        Node<E> current = head.get();
        while(current!=null) {
            current=current.next;
            counter++;
        }
//        System.out.println(head.get()+" - counter = " + counter);
        return counter;
    }
    @Override public void println() {
        System.out.println(head.get()!=null ? head.get().toString() : "null");
    }

    @Override public void clear() {
        head.set(null);
    }

    @Override public void push(E e, long sleep_at_some_point) {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            sleep(sleep_at_some_point/3);
            oldHead = head.get();
            sleep(sleep_at_some_point/3);
            newHead = new Node<>(e, oldHead);
            sleep(sleep_at_some_point/3);
        } while(!head.compareAndSet(oldHead, newHead));
    }

    @Override public E pop(long sleep_at_some_point) {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            sleep(sleep_at_some_point/3);
            oldHead = head.get();
            sleep(sleep_at_some_point/3);
            if(oldHead==null)
                return null;
            else
                newHead = oldHead.next;
            sleep(sleep_at_some_point/3);
        } while(! head.compareAndSet(oldHead, newHead));

        return oldHead.val;
    }

    @Override public E peek(long sleep_at_some_point) {
        sleep(sleep_at_some_point/2);
        Node<E> headNode = head.get();
        sleep(sleep_at_some_point/2);
        return headNode==null? null: headNode.val;
    }
}