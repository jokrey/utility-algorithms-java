package jokrey.utilities.simple.data_structure.iterators;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class IteratorUtilities {
    public static <E, R> Iterator<R> map(Iterator<E> iterator, Function<E, R> conv) {
        return new Iterator<R>() {
            @Override public boolean hasNext() {
                return iterator.hasNext();
            }
            @Override public R next() {
                return conv.apply(iterator.next());
            }
            @Override public String toString() {
                return "map of "+iterator;
            }
        };
    }

    public static <E>Iterator<E> reverseIterator(List<E> list) {
        return new Iterator<E>() {
            int i = list.size() - 1;
            @Override public boolean hasNext() {
                return i >= 0;
            }
            @Override public E next() {
                return list.get(i--);
            }
            @Override public String toString() {
                return "reverse of "+list;
            }
        };
    }
}
