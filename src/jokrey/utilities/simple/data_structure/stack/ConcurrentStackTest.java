package jokrey.utilities.simple.data_structure.stack;

import jokrey.utilities.debug_analysis_helper.AverageCallTimeMarker;
import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author jokrey
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConcurrentStackTest {
//    @Test public void singleThread_lockless() {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFStack<String> stack = new LFStack<>();
//        run_single_thread_test(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleThread_locked() {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LockedStack<String> stack = new LockedStack<>();
//        run_single_thread_test(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleThread_synchronized() {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        SynchronizedStack<String> stack = new SynchronizedStack<>();
//        run_single_thread_test(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleThread_notLocked() {//(expected = AssertionError.class)
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LinkedStack<String> stack = new LinkedStack<>();
//        run_single_thread_test(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleThread_x_print_round() {
//        AverageCallTimeMarker.print_all("STACK");
//    }

    @Test public void manyWritersManyReaders_lockless() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LFStack<String> stack = new LFStack<>();
        run_ManyWritersManyReaders(stack);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_locked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LockedStack<String> stack = new LockedStack<>();
        run_ManyWritersManyReaders(stack);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_synchronized() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        SynchronizedStack<String> stack = new SynchronizedStack<>();
        run_ManyWritersManyReaders(stack);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test//(expected = AssertionError.class)
    public void manyWritersManyReaders_notLocked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LinkedStack<String> stack = new LinkedStack<>();
        run_ManyWritersManyReaders(stack);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_x_print_round() {
        AverageCallTimeMarker.print_all("STACK");
    }
//
//    @Test public void manyReaders_lockless() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFStack<String> stack = new LFStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_locked() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LockedStack<String> stack = new LockedStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_synchronized() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        SynchronizedStack<String> stack = new SynchronizedStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_notLocked() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LinkedStack<String> stack = new LinkedStack<>();
//        run_WriteOnceBeforeManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void manyReaders_x_print_round() {
//        AverageCallTimeMarker.print_all("STACK");
//    }
//
//    @Test public void singleWriterManyReaders_lockless() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFStack<String> stack = new LFStack<>();
//        run_SingleWriterManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleWriterManyReaders_locked() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LockedStack<String> stack = new LockedStack<>();
//        run_SingleWriterManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleWriterManyReaders_synchronized() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        SynchronizedStack<String> stack = new SynchronizedStack<>();
//        run_SingleWriterManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleWriterManyReaders_notLocked() throws Throwable {//(expected = AssertionError.class)
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LinkedStack<String> stack = new LinkedStack<>();
//        run_SingleWriterManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void singleWriterManyReaders_x_print_round() {
//        AverageCallTimeMarker.print_all("STACK");
//    }
//
//    @Test public void threadSuspension_ManyWritersManyReaders_lockless() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFStack<String> stack = new LFStack<>();
//        run_suspendThreads_ManyWritersManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void threadSuspension_ManyWritersManyReaders_locked() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LockedStack<String> stack = new LockedStack<>();
//        run_suspendThreads_ManyWritersManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void threadSuspension_ManyWritersManyReaders_synchronized() throws Throwable {
//        String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//        AverageCallTimeMarker.mark_call_start(method_name);
//        SynchronizedStack<String> stack = new SynchronizedStack<>();
//        run_suspendThreads_ManyWritersManyReaders(stack);
//        AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void threadSuspension_ManyWritersManyReaders_notLocked() throws Throwable {//(expected = AssertionError.class)
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LinkedStack<String> stack = new LinkedStack<>();
//        run_suspendThreads_ManyWritersManyReaders(stack);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
//    @Test public void threadSuspension_ManyWritersManyReaders_x_print_round() {
//        AverageCallTimeMarker.print_all("STACK");
//    }


    public void run_single_thread_test(Stack<String> stack) {
        int iterations = 10000000;
        for(int i=0;i<iterations;i++) {
            stack.push(String.valueOf(i));
        }

        assertEquals(iterations, stack.size());

        for(int i=0;i<iterations;i++) {
            assertEquals(String.valueOf(iterations-1), stack.peek());
        }

        assertEquals(iterations, stack.size());

        for(int i=iterations-1;i>=0;i--) {
            assertEquals(String.valueOf(i), stack.pop());
        }

        assertEquals(0, stack.size());
    }
    public void run_ManyWritersManyReaders(Stack<String> stack) throws Throwable {
        int nThreads = 10000;
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                stack.push(fi + "");
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, stack.size());

        pool = new ConcurrentPoolTester(nThreads);

        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = stack.peek();
                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, stack.size());

        pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = stack.pop();
                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(0, stack.size());
    }
    public void run_SingleWriterManyReaders(Stack<String> stack) throws Throwable {
        int nThreads = 1000;
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
//        stack.push("initial");
        pool.execute(() -> {
            for(int i=0;i<nThreads*nThreads;i++) {
                if (i%2==0)         stack.pop();
                else                stack.push(i + "");

//                stack.println();
            }
        });
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                for(int i2=0;i2<nThreads;i2++) {
                     String val = stack.peek();
//                   System.out.println("val: " + val);
                }
//                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

//        assertEquals(0, stack.size());
        stack.println();
    }
    public void run_WriteOnceBeforeManyReaders(Stack<String> stack) throws Throwable {
        int nThreads = 10000;
        stack.push("initial");
        for(int i=0;i<1000;i++) {
            stack.push(i + "");
        }
        stack.push("top");
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = stack.peek();
                assertEquals("top", val);
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(1002, stack.size());
    }
    public void run_suspendThreads_ManyWritersManyReaders(Stack<String> stack) throws Throwable {
        int nThreads = 1000;
        int suspendEveryNthThread = 100;
        int suspendFor = 500;

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
                String val;
                if(fi %suspendEveryNthThread == 0)
                    val = stack.peek(suspendFor);
                else
                    val = stack.peek();
                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, stack.size());

        pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                String val;
                if(fi %suspendEveryNthThread == 0)
                    val = stack.pop(suspendFor);
                else
                    val = stack.pop();
                assertNotNull(val); //cannot really assert anything else..
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
}