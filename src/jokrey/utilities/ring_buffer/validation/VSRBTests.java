package jokrey.utilities.ring_buffer.validation;

import jokrey.utilities.encoder.tag_based.tests.performance.GenericPerformanceTest;
import jokrey.utilities.ring_buffer.VarSizedRingBuffer;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class VSRBTests {
    @Test
    public void simpleElementsTest() {
        byte[] e = "elem".getBytes();
        numTestNONWRAPPINGONLY(i->e, 100, 1024);
    }
    @Test
    public void fillItPrintIt() {
        numTestNONWRAPPINGONLY(i -> (i+"abcdefghijklmnopqrstuvxyz0123456789").getBytes(), 10, 1024);
    }

    @Test
    public void wrapTestSimple() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+10);
        VarSizedRingBuffer vsrb = new VarSizedRingBuffer(store, VarSizedRingBuffer.START+10);

        afterWriteStateTest(store, vsrb, "1", Arrays.asList("1"));
        afterWriteStateTest(store, vsrb, "2", Arrays.asList("1", "2"));
        afterWriteStateTest(store, vsrb, "3", Arrays.asList("1", "2", "3"));
        afterWriteStateTest(store, vsrb, "4", Arrays.asList("2", "3", "4"));
        afterWriteStateTest(store, vsrb, "5", Arrays.asList("3", "4", "5"));
        afterWriteStateTest(store, vsrb, "6", Arrays.asList("4", "5", "6"));
        afterWriteStateTest(store, vsrb, "7", Arrays.asList("5", "6", "7"));
    }

    @Test
    public void varSizeWrapAroundTesting() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+11);
        VarSizedRingBuffer vsrb = new VarSizedRingBuffer(store, VarSizedRingBuffer.START+11);

        afterWriteStateTest(store, vsrb, "1", Arrays.asList("1"));
        afterWriteStateTest(store, vsrb, "22", Arrays.asList("1", "22"));
        afterWriteStateTest(store, vsrb, "333", Arrays.asList("333"));
        afterWriteStateTest(store, vsrb, "444", Arrays.asList("333", "444"));
        afterWriteStateTest(store, vsrb, "5", Arrays.asList("444", "5"));
        afterWriteStateTest(store, vsrb, "6", Arrays.asList("5", "6"));//NOTE - here 444, 5, 6 does not fit

        store = new ByteArrayStorage(VarSizedRingBuffer.START+11);
        vsrb = new VarSizedRingBuffer(store, VarSizedRingBuffer.START+11);

        afterWriteStateTest(store, vsrb, "444", Arrays.asList("444"));
        afterWriteStateTest(store, vsrb, "5", Arrays.asList("444", "5"));
        afterWriteStateTest(store, vsrb, "6", Arrays.asList("444", "5", "6"));//NOTE - here 444, 5, 6 fits(different mem layout)
        afterWriteStateTest(store, vsrb, "777777", Arrays.asList("6", "777777"));
        afterWriteStateTest(store, vsrb, "888888888", Arrays.asList("888888888"));

        try {
            afterWriteStateTest(store, vsrb, "9999999999", Arrays.asList("9999999999"));
            fail("could fit 10");
        } catch(IllegalArgumentException ignore) { } //supposed to happen
    }

    @Test
    public void restartWorks() {
        ByteArrayStorage store = new ByteArrayStorage(VarSizedRingBuffer.START+11);
        VarSizedRingBuffer vsrb = new VarSizedRingBuffer(store, VarSizedRingBuffer.START+11);

        afterWriteStateTest(store, vsrb, "4444", Arrays.asList("4444"));
        afterWriteStateTest(store, vsrb, "333", Arrays.asList("4444", "333"));

        vsrb = new VarSizedRingBuffer(store, VarSizedRingBuffer.START+11);
        assertEquals(Arrays.asList("4444", "333"), vsrb.iterator().collect().stream().map(String::new).collect(Collectors.toList()));
    }

    public static void afterWriteStateTest(TransparentBytesStorage store, VarSizedRingBuffer vsrb, String toAdd, List<String> expectedTotalContent) {
        vsrb.write(toAdd.getBytes());//takes up 3 bytes
        VSBRDebugPrint.printContents(vsrb, store, String::new);
        check(vsrb, expectedTotalContent);
    }
    public static void check(VarSizedRingBuffer vsrb, List<String> expectedTotalContent) {
        assertEquals(expectedTotalContent, vsrb.iterator().collect().stream().map(String::new).collect(Collectors.toList()));
    }


    @Test
    public void notEnoughSpaceDetectedTest() {
        int i = 1;
        try {
            for (; i < 100; i++) {
                TransparentBytesStorage store = new ByteArrayStorage(100);
                VarSizedRingBuffer vsrb = new VarSizedRingBuffer(store, 100);
                vsrb.write(GenericPerformanceTest.generate_utf8_conform_byte_array(i));
            }
            fail("too small not called");
        } catch (IllegalArgumentException ignore) {
            System.out.println("element size(bytes) of first not fitting = " + i);
        }//success
    }

    public static void numTestNONWRAPPINGONLY(Function<Integer, byte[]> deterministicGenerator, int num, int max) {
        TransparentBytesStorage store = new ByteArrayStorage(max);
        VarSizedRingBuffer vsrb = new VarSizedRingBuffer(store, max);

        for(int i=0;i<num;i++) {
            byte[] e = deterministicGenerator.apply(i);
            vsrb.write(e);
            VSBRDebugPrint.printContents(vsrb, store, String::new);

            Iterator<byte[]> iterator = vsrb.iterator();
            int startFinder = 0;
            for(;startFinder<i;startFinder++) {
                byte[] ee = deterministicGenerator.apply(startFinder);
                if(Arrays.equals(ee, iterator.next())) {
                    break;
                }
            }

            System.out.println("stored elements(num-startFinder) = " + (i - startFinder));

            iterator = vsrb.iterator();
            for(int ii=startFinder;ii<i;ii++) {
                byte[] ee = deterministicGenerator.apply(ii);
                assertArrayEquals(ee, iterator.next());
            }
        }
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
    public static void numTestWRAPPING(Function<Integer, byte[]> deterministicGenerator, int num, int max) {
        TransparentBytesStorage store = new ByteArrayStorage(max);
        VarSizedRingBuffer vsrb = new VarSizedRingBuffer(store, max);

        try {
            for (int i = 0; i < num; i++) {
                byte[] e = deterministicGenerator.apply(i);
                vsrb.write(e);
//                VSBRDebugPrint.printContents(vsrb, store, String::new);

                List<byte[]> iterator = vsrb.iterator().collect();
                for (int ii = 0; ii < iterator.size(); ii++) {
                    byte[] gen = deterministicGenerator.apply(i - ii);
                    byte[] got = iterator.get(iterator.size() - (ii + 1));
                    assertArrayEquals(gen, got);
                }
//                System.out.println("e.length = " + e.length);
            }
            System.out.println("vsrb.iterator().collect().size() = " + vsrb.iterator().collect().size());
        } finally {
            VSBRDebugPrint.printContents(vsrb, store, String::new);
        }
    }
}
