package jokrey.utilities.ring_buffer.validation;

import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.encoder.tag_based.tests.performance.GenericPerformanceTest;
import jokrey.utilities.ring_buffer.VarSizedRingBuffer;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static jokrey.utilities.ring_buffer.validation.VSRBTests.afterWriteStateTest;
import static jokrey.utilities.ring_buffer.validation.VSRBTests.check;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Assumptions:
 *   TransparentBytesStorage given to vsrb is exclusively used
 *   delete and set operations must each be atomic (for FileStorage this can be reasonably 'assumed' for small enough elements)
 */
public class CrashConsistencyValidation {
    @Test
    public void testRemainsConsistent_withWRAP_CrashAfterTruncate() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+8);

        VarSizedRingBufferVALIDATIONVERSION vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);

        afterWriteStateTest(store, vsrb, "11", Arrays.asList("11")); //lieSize=4
        afterWriteStateTest(store, vsrb, "22", Arrays.asList("11", "22")); //lieSize=4
        afterWriteStateTest(store, vsrb, "3", Arrays.asList("22", "3")); //lieSize=3 (but has space 4, so cole!=lwl)

        byte[] bytesThatWillTriggerTruncateNow = "44444".getBytes();
        boolean crashed = vsrb.write(bytesThatWillTriggerTruncateNow, BreakdownPoint.AFTER_TRUNCATE);

        vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);
        VSBRDebugPrint.printContents(vsrb, store, String::new);
        check(vsrb, Arrays.asList("3"));
        assertTrue(crashed);

        //NOTE - we lost some data in the crash. But the latest added element remains. So order is consistent.
        // We need to truncate to a consistent state
        // we cannot truncate to newCole, because if it crashes after that:
        //    we would assume that at oldCole(which has not been overwritten), we find a valid li item - which we would no longer


        afterWriteStateTest(store, vsrb, "44444", Arrays.asList("44444"));
    }


    @Test
    public void testRemainsConsistent_withWRAP_CrashAfterWritePre() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+8);

        VarSizedRingBufferVALIDATIONVERSION vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);

        afterWriteStateTest(store, vsrb, "11", Arrays.asList("11")); //lieSize=4
        afterWriteStateTest(store, vsrb, "22", Arrays.asList("11", "22")); //lieSize=4
        afterWriteStateTest(store, vsrb, "3", Arrays.asList("22", "3")); //lieSize=3 (but has space 4, so cole!=lwl)

        byte[] bytesThatWillTriggerTruncateNow = "44444".getBytes();
        boolean crashed = vsrb.write(bytesThatWillTriggerTruncateNow, BreakdownPoint.AFTER_WRITE_PRE);

        vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);
        VSBRDebugPrint.printContents(vsrb, store, String::new);
        check(vsrb, Arrays.asList("3"));
        assertTrue(crashed);

        afterWriteStateTest(store, vsrb, "44444", Arrays.asList("44444"));

        // We lost data in this crash. "22" is no longer there..
        // But state is consistent. We can read "3", and try to add "44444" again.
        //
        // What happened: we truncated the content size to 20 - a valid cole
        //                we we set cole to 23 - without appending to that yet (would have happened after crash)
        // At restart, we notice that cole > contentSize -> which cannot happen, unless crash -> we set cole=contentSize
        //     This is the same as the if it crashed after truncate, before write-cole
    }

    @Test
    public void testRemainsConsistent_withWRAP_CrashAfterWriteElement() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+8);

        VarSizedRingBufferVALIDATIONVERSION vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);

        afterWriteStateTest(store, vsrb, "11", Arrays.asList("11")); //lieSize=4
        afterWriteStateTest(store, vsrb, "22", Arrays.asList("11", "22")); //lieSize=4
        afterWriteStateTest(store, vsrb, "3", Arrays.asList("22", "3")); //lieSize=3 (but has space 4, so cole!=lwl)

        byte[] bytesThatWillTriggerTruncateNow = "44444".getBytes();
        boolean crashed = vsrb.write(bytesThatWillTriggerTruncateNow, BreakdownPoint.AFTER_WRITE_ELEMENT);

        vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);
        VSBRDebugPrint.printContents(vsrb, store, String::new);
        check(vsrb, Arrays.asList("44444"));
        assertTrue(crashed);

        afterWriteStateTest(store, vsrb, "5", Arrays.asList("5"));
    }

    @Test
    public void testRemainsConsistent_withWRAP_CrashAfterWriteAfter() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+8);

        VarSizedRingBufferVALIDATIONVERSION vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);

        afterWriteStateTest(store, vsrb, "11", Arrays.asList("11")); //lieSize=4
        afterWriteStateTest(store, vsrb, "22", Arrays.asList("11", "22")); //lieSize=4
        afterWriteStateTest(store, vsrb, "3", Arrays.asList("22", "3")); //lieSize=3 (but has space 4, so cole!=lwl)

        byte[] bytesThatWillTriggerTruncateNow = "44444".getBytes();
        boolean crashed = vsrb.write(bytesThatWillTriggerTruncateNow, BreakdownPoint.AFTER_WRITE_AFTER);

        vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+8);
        VSBRDebugPrint.printContents(vsrb, store, String::new);
        check(vsrb, Arrays.asList("44444"));
        assertTrue(crashed);

        afterWriteStateTest(store, vsrb, "5", Arrays.asList("5"));
    }


    @Test
    public void testConsistency_noWRAP() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+1024);

        genTestSomeData(store, 1024);

        VarSizedRingBufferVALIDATIONVERSION vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+1024);
        byte[] bytesThatWillTriggerTruncateNow = "44444".getBytes();
        boolean crashed = vsrb.write(bytesThatWillTriggerTruncateNow, BreakdownPoint.AFTER_WRITE_AFTER);
        assertTrue(crashed);

        afterWriteStateTest(store, vsrb, "5555", Arrays.asList("1111111", "222222222222", "3", "44444", "5555"));

    }

    private void genTestSomeData(TransparentBytesStorage store, int max) {
        VarSizedRingBufferVALIDATIONVERSION vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, VarSizedRingBuffer.START+max);

        afterWriteStateTest(store, vsrb, "1111111", Arrays.asList("1111111")); //lieSize=4
        afterWriteStateTest(store, vsrb, "222222222222", Arrays.asList("1111111", "222222222222")); //lieSize=4
        afterWriteStateTest(store, vsrb, "3", Arrays.asList("1111111", "222222222222", "3")); //lieSize=3 (but has space 4, so cole!=lwl)
    }




    @Test
    public void fillItPrintItWrapIt() {
        for(int max=55;max<1000;max+=19) {
            for (int num = 0; num < 100; num++) {
                numTestWRAPPING(i -> (i + "abcdefghijklmnopqrstuvxyz0123456789").getBytes(), 100, 1024);
            }
        }
    }
    @Test
    public void fillItPrintItWrapItVarSized() {
        for(int max=55;max<1000;max+=19) {
            for (int num = 0; num < 100; num++) {
                numTestWRAPPING(i -> GenericPerformanceTest.generate_utf8_conform_byte_array(i+10), 100, 1024);
            }
        }
    }
    public void numTestWRAPPING(Function<Integer, byte[]> deterministicGenerator, int num, int max) {
        TransparentBytesStorage store = new ByteArrayStorage(max);
        VarSizedRingBufferVALIDATIONVERSION vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, max);

        Random r = new Random(1736485445);

        try {
            for (int i = 0; i < num; i++) {
                byte[] e = deterministicGenerator.apply(i);

                List<byte[]> iterator;
                if (r.nextInt(22) == 0) {
                    System.err.println("e = " + new String(e));
                    int nextInt = r.nextInt(BreakdownPoint.values().length);
                    System.err.println("next-int = " + nextInt);
                    System.err.println("BreakdownPoint.values() = " + Arrays.toString(BreakdownPoint.values()));
                    BreakdownPoint crash = BreakdownPoint.values()[nextInt];
                    vsrb.write(e, crash);

                    vsrb = new VarSizedRingBufferVALIDATIONVERSION(store, max);//THIS IS THE RECOVERY MECHANISM
                    iterator = vsrb.iterator().collect();
                    if (!Arrays.equals(e, last(iterator))) {
                        i--;
                        System.err.println("lost data");
                    }
                    System.err.println("crash = " + crash);
                } else {
                    vsrb.append(e);
                    iterator = vsrb.iterator().collect();
                }

                for (int ii = 0; ii < iterator.size(); ii++) {
                    byte[] gen = deterministicGenerator.apply(i - ii);
                    byte[] got = iterator.get(iterator.size() - (ii + 1));
                    assertArrayEquals(gen, got);
                }
            }
        } finally {
            VSBRDebugPrint.printContents(vsrb, store, String::new);
        }
    }

    private <T>T last(List<T> list) {
        return list.get(list.size()-1);
    }


    enum BreakdownPoint {
        AFTER_TRUNCATE, AFTER_WRITE_PRE, AFTER_WRITE_ELEMENT, AFTER_WRITE_AFTER
    }

    private class VarSizedRingBufferVALIDATIONVERSION extends VarSizedRingBuffer {
        public VarSizedRingBufferVALIDATIONVERSION(TransparentBytesStorage storage, long max) {
            super(storage, max);
        }

        //Returns whether the crash was triggered
        public boolean write(byte[] e, BreakdownPoint crash) {
            byte[] li = LIbae.generateLI(e.length);
            int lieSize = li.length + e.length;
            System.out.println("write(e) before - lwl(" + lwl+"), cole("+cole+"), lieSize("+lieSize+")");

            long previousLWL = lwl;
            long previousCOLE = cole;
            long previousContentSize = storage.contentSize();

            //calc new-lwl
            long newLwl = lwl + lieSize;
            if (newLwl > max) {
                newLwl = START + lieSize;
                if(newLwl > max)
                    throw new IllegalArgumentException("element to large");
                if (cole > lwl) {//only if not recovering
                    truncateToCOLE();
                    if(crash == BreakdownPoint.AFTER_TRUNCATE) return true;
                }
            }


            //calc new-cole
            long newCole;
            if (cole == storage.contentSize()) {
                if (newLwl >= storage.contentSize()) {
                    newCole = newLwl;
                } else {
                    long[] liBounds = readLIBoundsAt(START);
                    if(liBounds == null) throw new IllegalStateException("could not read li at start (corrupt data)");
                    newCole = liBounds[1];
                }
            } else {
                newCole = cole;
            }
            while (newLwl > newCole) {
                if(newCole >= storage.contentSize()) {
                    newCole = newLwl;
                } else {
                    long[] liBounds = readLIBoundsAt(newCole);
                    //the following line should never be called, unless corrupt archive - because newCole just jumps and writeAt is always in bounds
                    if (liBounds == null)
                        throw new IllegalStateException("could not read li at newCole(" + newCole + ") (corrupt data)");
                    newCole = liBounds[1];
                }
            }

            long writeAt = newLwl - lieSize;
            //write order hella important for free and automatic crash recovery
            writePre(newCole, newLwl, writeAt);
            if(crash == BreakdownPoint.AFTER_WRITE_PRE) {
                System.out.println("cole(written) = " + (newCole));
                System.out.println("writeAt = " + (newLwl - lieSize));
                System.out.println("did not wrote lie (size= " + lieSize+")");

                System.out.println("lwl = " + (lwl));
                System.out.println("newLwl(unwritten) = " + (newLwl));
                return true;
            }
            writeElem(writeAt, li, e);
            if(crash == BreakdownPoint.AFTER_WRITE_ELEMENT) {
                System.out.println("cole(written) = " + (newCole));
                System.out.println("writeAt = " + (newLwl - lieSize));
                System.out.println("wrote lie = " + lieSize);

                System.out.println("lwl = " + (lwl));
                System.out.println("newLwl(unwritten) = " + (newLwl));
                return true;
            }
            writePost(newCole, newLwl);
            if(crash == BreakdownPoint.AFTER_WRITE_AFTER) {
                System.out.println("cole(written) = " + (cole));
                System.out.println("writeAt = " + (newLwl - lieSize));
                System.out.println("wrote = " + lieSize);

                System.out.println("lwl = " + (lwl));
                System.out.println("newLwl(unwritten) = " + (newLwl));
                return true;
            }
            System.out.println("write(e) after - lwl(" + lwl+"), cole("+cole+"), lieSize("+lieSize+"), at("+(newLwl - lieSize)+")");
            return false;
        }
    }
}
