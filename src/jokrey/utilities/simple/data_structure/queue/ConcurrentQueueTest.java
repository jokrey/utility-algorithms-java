package jokrey.utilities.simple.data_structure.queue;

import jokrey.utilities.debug_analysis_helper.AverageCallTimeMarker;
import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * @author jokrey
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConcurrentQueueTest {
//    @Test public void singleThread_lockless() {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFQueue<String> queue = new LFQueue<>();
//        run_single_thread_test(queue);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
    @Test public void singleThread_locked() {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LockedQueue<String> queue = new LockedQueue<>();
        run_single_thread_test(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void singleThread_synchronized() {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        SynchronizedQueue<String> queue = new SynchronizedQueue<>();
        run_single_thread_test(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void singleThread_notLocked() {//(expected = AssertionError.class)
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LinkedQueue<String> queue = new LinkedQueue<>();
        run_single_thread_test(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void singleThread_x_print_round() {
        AverageCallTimeMarker.print_all("QUEUE");
    }

//    @Test public void manyWritersManyReaders_lockless() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFQueue<String> queue = new LFQueue<>();
//        run_ManyWritersManyReaders(queue);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
    @Test public void manyWritersManyReaders_locked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LockedQueue<String> queue = new LockedQueue<>();
        run_ManyWritersManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_synchronized() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        SynchronizedQueue<String> queue = new SynchronizedQueue<>();
        run_ManyWritersManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test//(expected = AssertionError.class)
    public void manyWritersManyReaders_notLocked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LinkedQueue<String> queue = new LinkedQueue<>();
        run_ManyWritersManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyWritersManyReaders_x_print_round() {
        AverageCallTimeMarker.print_all("QUEUE");
    }

//    @Test public void manyReaders_lockless() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFQueue<String> queue = new LFQueue<>();
//        run_WriteOnceBeforeManyReaders(queue);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
    @Test public void manyReaders_locked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LockedQueue<String> queue = new LockedQueue<>();
        run_WriteOnceBeforeManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyReaders_synchronized() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        SynchronizedQueue<String> queue = new SynchronizedQueue<>();
        run_WriteOnceBeforeManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyReaders_notLocked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LinkedQueue<String> queue = new LinkedQueue<>();
        run_WriteOnceBeforeManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void manyReaders_x_print_round() {
        AverageCallTimeMarker.print_all("QUEUE");
    }

//    @Test public void singleWriterManyReaders_lockless() throws Throwable {
//        String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//        AverageCallTimeMarker.mark_call_start(method_name);
//        LFQueue<String> queue = new LFQueue<>();
//        run_SingleWriterManyReaders(queue);
//        AverageCallTimeMarker.mark_call_end(method_name);
//    }
    @Test public void singleWriterManyReaders_locked() throws Throwable {
        String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
        AverageCallTimeMarker.mark_call_start(method_name);
        LockedQueue<String> queue = new LockedQueue<>();
        run_SingleWriterManyReaders(queue);
        AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void singleWriterManyReaders_synchronized() throws Throwable {
        String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
        AverageCallTimeMarker.mark_call_start(method_name);
        SynchronizedQueue<String> queue = new SynchronizedQueue<>();
        run_SingleWriterManyReaders(queue);
        AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void singleWriterManyReaders_notLocked() throws Throwable {//(expected = AssertionError.class)
        String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
        AverageCallTimeMarker.mark_call_start(method_name);
        LinkedQueue<String> queue = new LinkedQueue<>();
        run_SingleWriterManyReaders(queue);
        AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void singleWriterManyReaders_x_print_round() {
        AverageCallTimeMarker.print_all("QUEUE");
    }

//    @Test public void threadSuspension_ManyWritersManyReaders_lockless() throws Throwable {
//            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
//            AverageCallTimeMarker.mark_call_start(method_name);
//        LFQueue<String> queue = new LFQueue<>();
//        run_suspendThreads_ManyWritersManyReaders(queue);
//            AverageCallTimeMarker.mark_call_end(method_name);
//    }
    @Test public void threadSuspension_ManyWritersManyReaders_locked() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LockedQueue<String> queue = new LockedQueue<>();
        run_suspendThreads_ManyWritersManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void threadSuspension_ManyWritersManyReaders_synchronized() throws Throwable {
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        SynchronizedQueue<String> queue = new SynchronizedQueue<>();
        run_suspendThreads_ManyWritersManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void threadSuspension_ManyWritersManyReaders_notLocked() throws Throwable {//(expected = AssertionError.class)
            String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
            AverageCallTimeMarker.mark_call_start(method_name);
        LinkedQueue<String> queue = new LinkedQueue<>();
        run_suspendThreads_ManyWritersManyReaders(queue);
            AverageCallTimeMarker.mark_call_end(method_name);
    }
    @Test public void threadSuspension_ManyWritersManyReaders_x_print_round() {
        AverageCallTimeMarker.print_all("QUEUE");
    }


    public static void run_single_thread_test(Queue<String> queue) {
        run_single_thread_test(100, 100000, queue, s->s, s->s);
    }
    public static <E>void run_single_thread_test(int redos, int iterations, Queue<E> queue, Function<String, E> conv, Function<E, String> convBack) {
        queue.clear();
        for(int redoForTime=0;redoForTime<redos;redoForTime++) {
            for (int i = 0; i < iterations; i++) {
                queue.enqueue(conv.apply(String.valueOf(i)));
            }

            assertEquals(iterations, queue.size());

            for (int i = 0; i < iterations; i++) {
                queue.peek();
            }

            assertEquals(iterations, queue.size());

            for (int i = 0; i < iterations; i++) {
                assertEquals(String.valueOf(i), convBack.apply(queue.dequeue()));
            }

            assertEquals(0, queue.size());
        }
    }
    public static void run_ManyWritersManyReaders(Queue<String> queue) throws Throwable {
        run_ManyWritersManyReaders(queue, s->s, s->s);
    }
    public static <E>void run_ManyWritersManyReaders(Queue<E> queue, Function<String, E> conv, Function<E, String> convBack) throws Throwable {
        queue.clear();
        int nThreads = 10000;
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                queue.enqueue(conv.apply(fi + ""));
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, queue.size());

        pool = new ConcurrentPoolTester(nThreads);

        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = convBack.apply(queue.peek());
                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, queue.size());

        pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = convBack.apply(queue.dequeue());
                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(0, queue.size());
    }
    public static void run_SingleWriterManyReaders(Queue<String> queue) throws Throwable {
        run_SingleWriterManyReaders(queue, s->s, s->s);
    }
    public static <E>void run_SingleWriterManyReaders(Queue<E> queue, Function<String, E> conv, Function<E, String> convBack) throws Throwable {
        queue.clear();
        int nThreads = 100;
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
//        queue.enqueue("initial");
        pool.execute(() -> {
            for(int i=0;i<nThreads;i++) {
                if (i % 2 == 0) queue.enqueue(conv.apply(i + ""));
                else            queue.dequeue();
            }
        });
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                for(int i2=0;i2<nThreads;i2++) {
                    queue.peek();
                }
//                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(0, queue.size());
    }
    public static void run_WriteOnceBeforeManyReaders(Queue<String> queue) throws Throwable {
        run_WriteOnceBeforeManyReaders(queue, s->s, s->s);
    }
    public static <E>void run_WriteOnceBeforeManyReaders(Queue<E> queue, Function<String, E> conv, Function<E, String> convBack) throws Throwable {
        queue.clear();
        int nThreads = 10000;
        queue.enqueue(conv.apply("initial"));
        for(int i=0;i<1000;i++) {
            queue.enqueue(conv.apply(i + ""));
        }
        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            pool.execute(() -> {
                String val = convBack.apply(queue.peek());
                assertEquals("initial", val);
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(1001, queue.size());
    }
    public void run_suspendThreads_ManyWritersManyReaders(EvaluableQueue<String> queue) throws Throwable {
        int nThreads = 3000;
        int suspendEveryNthThread = 100;
        int suspendFor = 250;

        ConcurrentPoolTester pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                if(fi %suspendEveryNthThread == 0)
                    queue.enqueue(String.valueOf(fi), suspendFor);
                else
                    queue.enqueue(String.valueOf(fi));
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, queue.size());

        pool = new ConcurrentPoolTester(nThreads);

        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                String val;
                if(fi %suspendEveryNthThread == 0)
                    val = queue.peek(suspendFor);
                else
                    val = queue.peek();
                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(nThreads, queue.size());

        pool = new ConcurrentPoolTester(nThreads);
        for(int i=0;i<nThreads;i++) {
            int fi = i;
            pool.execute(() -> {
                String val;
                if(fi %suspendEveryNthThread == 0)
                    val = queue.dequeue(suspendFor);
                else
                    val = queue.dequeue();
                assertNotNull(val); //cannot really assert anything else..
            });
        }
        pool.waitForShutdownOrException();

        assertEquals(0, queue.size());
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
