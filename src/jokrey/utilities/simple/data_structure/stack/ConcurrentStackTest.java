package jokrey.utilities.simple.data_structure.stack;

import jokrey.utilities.debug_analysis_helper.BoxPlotDataGatherer;
import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.function.Function;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author jokrey
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConcurrentStackTest {
    @Test public void singleThread_lockless() {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LFStack<String> stack = new LFStack<>();
        singleThreadTest(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleThread_locked() {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LockedStack<String> stack = new LockedStack<>();
        singleThreadTest(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleThread_synchronized() {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        SynchronizedStack<String> stack = new SynchronizedStack<>();
        singleThreadTest(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleThread_notLocked() {//(expected = AssertionError.class)
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LinkedStack<String> stack = new LinkedStack<>();
        singleThreadTest(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleThread_x_print_round() {
        BoxPlotDataGatherer.print_all();
    }

    @Test public void manyWritersManyReaders_lockless() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LFStack<String> stack = new LFStack<>();
        multipleWritersMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_locked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LockedStack<String> stack = new LockedStack<>();
        multipleWritersMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_synchronized() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        SynchronizedStack<String> stack = new SynchronizedStack<>();
        multipleWritersMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test//(expected = AssertionError.class)
    public void manyWritersManyReaders_notLocked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LinkedStack<String> stack = new LinkedStack<>();
        multipleWritersMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_x_print_round() {
        BoxPlotDataGatherer.print_all();
    }
//
//    @Test public void manyReaders_lockless() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            BoxPlotDataGatherer.mark_call_start(method_name);
//        LFStack<String> stack = new LFStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            BoxPlotDataGatherer.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_locked() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            BoxPlotDataGatherer.mark_call_start(method_name);
//        LockedStack<String> stack = new LockedStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            BoxPlotDataGatherer.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_synchronized() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            BoxPlotDataGatherer.mark_call_start(method_name);
//        SynchronizedStack<String> stack = new SynchronizedStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            BoxPlotDataGatherer.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_notLocked() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            BoxPlotDataGatherer.mark_call_start(method_name);
//        LinkedStack<String> stack = new LinkedStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            BoxPlotDataGatherer.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_x_print_round() {
//        BoxPlotDataGatherer.print_all();
//    }
//
    @Test public void singleWriterManyReaders_lockless() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LFStack<String> stack = new LFStack<>();
        singleWriterMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleWriterManyReaders_locked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LockedStack<String> stack = new LockedStack<>();
        singleWriterMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleWriterManyReaders_synchronized() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        SynchronizedStack<String> stack = new SynchronizedStack<>();
        singleWriterMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleWriterManyReaders_notLocked() throws Throwable {//(expected = AssertionError.class)
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LinkedStack<String> stack = new LinkedStack<>();
        singleWriterMultipleReaders(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void singleWriterManyReaders_x_print_round() {
        BoxPlotDataGatherer.print_all();
    }
//
    @Test public void threadSuspension_ManyWritersManyReaders_lockless() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LFStack<String> stack = new LFStack<>();
        multipleWritersMultipleReaders_withSuspension(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void threadSuspension_ManyWritersManyReaders_locked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LockedStack<String> stack = new LockedStack<>();
        multipleWritersMultipleReaders_withSuspension(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void threadSuspension_ManyWritersManyReaders_synchronized() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        SynchronizedStack<String> stack = new SynchronizedStack<>();
        multipleWritersMultipleReaders_withSuspension(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void threadSuspension_ManyWritersManyReaders_notLocked() throws Throwable {//(expected = AssertionError.class)
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            BoxPlotDataGatherer.mark_call_start(method_name);
        LinkedStack<String> stack = new LinkedStack<>();
        multipleWritersMultipleReaders_withSuspension(stack);
            BoxPlotDataGatherer.mark_call_end(method_name);
    }
    @Test public void threadSuspension_ManyWritersManyReaders_x_print_round() {
        BoxPlotDataGatherer.print_all();
    }


    public static void singleThreadTest(Stack<String> stack) {
        singleThreadTest(100000, stack, s->s, s->s);
    }
    public static <E>void singleThreadTest(int iterations, Stack<E> stack, Function<String, E> conv, Function<E, String> convBack) {
        stack.clear();
        for(int i=0;i<iterations;i++) {
            stack.push(conv.apply(String.valueOf(i)));
        }

        assertEquals(iterations, stack.size());

        for(int i=0;i<iterations;i++) {
            assertEquals(String.valueOf(iterations-1), convBack.apply(stack.top()));
        }

        assertEquals(iterations, stack.size());

        for(int i=iterations-1;i>=0;i--) {
            assertEquals(String.valueOf(i), convBack.apply(stack.pop()));
        }

        assertEquals(0, stack.size());
    }
    public static <E> void multipleWritersMultipleReaders(Stack<String> stack) throws Throwable {
        multipleWritersMultipleReaders(stack, s->s, s->s);
    }
    public static <E> void multipleWritersMultipleReaders(Stack<E> stack, Function<String, E> conv, Function<E, String> convBack) throws Throwable {
        stack.clear();
        int nThreads = 1000;
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                stack.push(conv.apply(fi + ""));
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, stack.size());

        pool = new ConcurrentPoolTester(nThreads);

        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = convBack.apply(stack.top());
                assertNotNull(val); //cannot assert anything else, actual state is nondeterministic
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, stack.size());

        pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = convBack.apply(stack.pop());
                assertNotNull(val); //cannot assert anything else, actual state is nondeterministic
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(0, stack.size());
    }
    public static void singleWriterMultipleReaders(Stack<String> stack) throws Throwable {
        singleWriterMultipleReaders(stack, s -> s, s -> s);
    }
    public static <E>void singleWriterMultipleReaders(Stack<E> stack, Function<String, E> conv, Function<E, String> convBack) throws Throwable {
        stack.clear();
        stack.push(conv.apply("former tos"));
        stack.push(conv.apply("tos"));

        int nThreads = 1000;
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        pool.execute(() -> {
            for(int i=0;i<nThreads*nThreads;i++) {
                if (i%2==0)         stack.pop();
                else                stack.push(conv.apply(i + ""));
            }
        });
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                for(int i2=0;i2<nThreads;i2++) {
                    String val = convBack.apply(stack.top()); //cannot assert anything else, actual state is nondeterministic
                    assertNotNull(val);
                }
            });
        }
        pool.waitForShutdownOrException();
    }
    public static void run_WriteOnceBeforeManyReaders(Stack<String> stack) throws Throwable {
        run_WriteOnceBeforeManyReaders(stack, s -> s, s -> s);
    }
    public static <E>void run_WriteOnceBeforeManyReaders(Stack<E> stack, Function<String, E> conv, Function<E, String> convBack) throws Throwable {
        stack.clear();
        int nThreads = 500;
        stack.push(conv.apply("initial"));
        for(int i=0;i<1000;i++) {
            stack.push(conv.apply(i + ""));
        }
        stack.push(conv.apply("top"));
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = convBack.apply(stack.top());
                assertEquals("top", val);
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(1002, stack.size());
    }
    public void multipleWritersMultipleReaders_withSuspension(EvaluableStack<String> stack) throws Throwable {
        int nThreads = 500;
        int suspendEveryNthThread = 100;
        int suspendFor = 250;

        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                if(fi %suspendEveryNthThread == 0)
                    stack.push(String.valueOf(fi), suspendFor);
                else
                    stack.push(String.valueOf(fi));
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, stack.size());

        pool = new ConcurrentPoolTester(nThreads);

        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                String val = (fi %suspendEveryNthThread == 0)? stack.top(suspendFor) : stack.top();
                assertNotNull(val); //cannot assert anything else, actual state is nondeterministic
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, stack.size());

        pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                String val = (fi %suspendEveryNthThread == 0)? stack.pop(suspendFor) : stack.pop();
                assertNotNull(val); //cannot assert anything else, actual state is nondeterministic
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(0, stack.size());
    }

    public static void pl(Object s) {
        System.out.println(s);
    }
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {  }
    }
    public static int rand(int min, int max) {
        return (int)(Math.random() * ( (max - min) + 1)) + min;
    }


    public static <E> void simpleStackTest(Stack<E> stack, Function<String, E> conv, Function<E, String> convBack) {
        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());

        stack.push(conv.apply("1"));
        stack.push(conv.apply("2"));
        stack.push(conv.apply("3"));
        stack.push(conv.apply("4"));
        stack.push(conv.apply("5"));
        stack.push(conv.apply("6"));
        assertEquals(6, stack.size());
        assertEquals("6", convBack.apply(stack.top()));

        assertEquals("6", convBack.apply(stack.pop()));
        assertEquals("5", convBack.apply(stack.pop()));
        assertEquals("4", convBack.apply(stack.pop()));
        assertEquals("3", convBack.apply(stack.pop()));
        assertEquals("2", convBack.apply(stack.pop()));

        assertEquals("1", convBack.apply(stack.top()));
        stack.push(conv.apply("22"));
        assertEquals("22", convBack.apply(stack.top()));
        assertEquals("22", convBack.apply(stack.pop()));
        assertEquals("1", convBack.apply(stack.pop()));

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        stack.push(conv.apply("111"));
        stack.push(conv.apply("222"));
        stack.push(conv.apply("333"));
        assertEquals(3, stack.size());

        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }
}