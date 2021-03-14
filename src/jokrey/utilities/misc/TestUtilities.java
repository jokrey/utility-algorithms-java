package jokrey.utilities.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.fail;

public class TestUtilities {
    public static <E> List<E> collectToList(Iterator<E> iterator) {
        List<E> list = new ArrayList<>();
        while(iterator.hasNext())
            list.add(iterator.next());
        return list;
    }

    public static void assertIteratorEquality(Iterator<?>... iterators) {
        if(iterators.length == 0) return;
        for(int counter=0;;counter++) {
            boolean hasNext = getOrFail(iterators.length, "at "+counter, i -> iterators[i].hasNext());
            if(!hasNext) break;
            Object next = getOrFail(iterators.length, "at "+counter, i -> iterators[i].next());
        }
    }

    public static<E, R> E getOrFail(int max, String additionalFailString, Function<Integer, E> supply) {
        List<E> supplied = new ArrayList<>();
        for(int i=0;i<max;i++)
            supplied.add(supply.apply(i));

        E expectation = supplied.get(0);
        for(E s:supplied) {
            if(!expectation.equals(s)) {
                StringBuilder failStringBuilder = new StringBuilder().append(additionalFailString).append(" - ");
                for (int i = 0; i < supplied.size(); i++) {
                    E sb = supplied.get(i);
                    failStringBuilder.append(i).append("(").append(sb).append("), ");
                }
                fail(failStringBuilder.delete(failStringBuilder.length()-2, failStringBuilder.length()).toString());
            }
        }
        return expectation;
    }
}
