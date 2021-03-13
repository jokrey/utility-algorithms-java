package jokrey.utilities.ring_buffer.validation;

import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.encoder.tag_based.tests.performance.GenericPerformanceTest;
import jokrey.utilities.ring_buffer.VarSizedRingBufferQueueOnly;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class VSRBTests {
    @Test
    public void simpleElementsTest() {
        byte[] e = "elem".getBytes();
        numTestNONWRAPPINGONLY(i->e, 10, 100);
    }
    @Test
    public void fillItPrintIt() {
        numTestNONWRAPPINGONLY(i -> (i+"abcdefghijklmnopqrstuvxyz0123456789").getBytes(), 10, 1024);
    }

    @Test
    public void wrapTestSimple() {
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBufferQueueOnly.START+10);
        VarSizedRingBufferQueueOnly vsrb = new VarSizedRingBufferQueueOnly(store, VarSizedRingBufferQueueOnly.START+10);

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
        TransparentBytesStorage store = new ByteArrayStorage(VarSizedRingBufferQueueOnly.START+11);
        VarSizedRingBufferQueueOnly vsrb = new VarSizedRingBufferQueueOnly(store, VarSizedRingBufferQueueOnly.START+11);

        afterWriteStateTest(store, vsrb, "1", Arrays.asList("1"));
        afterWriteStateTest(store, vsrb, "22", Arrays.asList("1", "22"));
        afterWriteStateTest(store, vsrb, "333", Arrays.asList("333"));
        afterWriteStateTest(store, vsrb, "444", Arrays.asList("333", "444"));
        afterWriteStateTest(store, vsrb, "5", Arrays.asList("444", "5"));
        afterWriteStateTest(store, vsrb, "6", Arrays.asList("5", "6"));//NOTE - here 444, 5, 6 does not fit

        store = new ByteArrayStorage(VarSizedRingBufferQueueOnly.START+11);
        vsrb = new VarSizedRingBufferQueueOnly(store, VarSizedRingBufferQueueOnly.START+11);

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
        ByteArrayStorage store = new ByteArrayStorage(VarSizedRingBufferQueueOnly.START+11);
        VarSizedRingBufferQueueOnly vsrb = new VarSizedRingBufferQueueOnly(store, VarSizedRingBufferQueueOnly.START+11);

        afterWriteStateTest(store, vsrb, "4444", Arrays.asList("4444"));
        afterWriteStateTest(store, vsrb, "333", Arrays.asList("4444", "333"));

        vsrb = new VarSizedRingBufferQueueOnly(store, VarSizedRingBufferQueueOnly.START+11);
        assertEquals(Arrays.asList("4444", "333"), VSBRDebugPrint.elementsToList(vsrb, String::new));
    }

    public static void afterWriteStateTest(TransparentBytesStorage store, VarSizedRingBufferQueueOnly vsrb, String toAdd, List<String> expectedTotalContent) {
        boolean couldAdd = vsrb.append(toAdd.getBytes());//takes up 3 bytes
        if(!couldAdd) throw new IllegalArgumentException("element too large for vrsb: tooAdd("+toAdd+")");
        VSBRDebugPrint.printContents("After adding toAdd("+toAdd+")", vsrb, store, String::new);
        check(vsrb, expectedTotalContent);
    }
    public static void check(VarSizedRingBufferQueueOnly vsrb, List<String> expectedTotalContent) {
        assertEquals(expectedTotalContent.size(), vsrb.size());//tests more the size function than anything else
        assertEquals(expectedTotalContent, VSBRDebugPrint.elementsToList(vsrb, String::new));
    }


    @Test
    public void wrongColeNotWrongTest() {
        ByteArrayStorage store = new ByteArrayStorage(VarSizedRingBufferQueueOnly.START+61);
        VarSizedRingBufferQueueOnly vsrb = new VarSizedRingBufferQueueOnly(store, VarSizedRingBufferQueueOnly.START+61);

        vsrb.append(ByteArrayStorage.getConcatenated("2523456789012345678901234567890123456789012345".getBytes(), new byte[] {0,0}));
        VSBRDebugPrint.printMemoryLayout(vsrb, store, String::new);

        vsrb.append(ByteArrayStorage.getConcatenated("2923456789012345678901234567890123".getBytes(), new byte[] {0,0}));
        VSBRDebugPrint.printMemoryLayout(vsrb, store, String::new);
    }

    @Test
    public void notEnoughSpaceDetectedTest() {
        for (int i = 1; i < 100; i++) {
            TransparentBytesStorage store = new ByteArrayStorage(100);
            VarSizedRingBufferQueueOnly vsrb = new VarSizedRingBufferQueueOnly(store, 100);
            boolean couldNotAdd = vsrb.append(GenericPerformanceTest.generate_utf8_conform_byte_array(i));
            if(couldNotAdd) {
                System.out.println("largest = " + i);
                return;//success
            }
        }
        fail("too small not called");
    }

    public static void numTestNONWRAPPINGONLY(Function<Integer, byte[]> deterministicGenerator, int num, int max) {
        TransparentBytesStorage store = new ByteArrayStorage(max);
        VarSizedRingBufferQueueOnly vsrb = new VarSizedRingBufferQueueOnly(store, max);

        for(int i=0;i<num;i++) {
            byte[] e = deterministicGenerator.apply(i);
            vsrb.append(e);
            VSBRDebugPrint.printContents("after appending e("+new String(e)+")", vsrb, store, String::new);

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
        String str = "abcdefghijklmnopqrstuvxyz0123456789";
        for(int max = VarSizedRingBufferQueueOnly.START + (str+"99").length()+ LIbae.generateLI((str+"99").length()).length; max<1000; max+=19) {
            for (int num = 0; num < 100; num++) {
                numTestWRAPPING((vsrb, i) -> (i + str).getBytes(), num, max);
            }
        }
    }
    @Test
    public void fillItPrintItWrapItVarSized() {
        for(int max=55;max<1000;max+=19) {
            for (int num = 0; num < 100; num++) {
                numTestWRAPPING(VSRBTests::utf8RandGen, num, max);
            }
        }
    }

    public static byte[] utf8RandGen(VarSizedRingBufferQueueOnly vsrb, int deterministicator) {
        Random r = new Random(Integer.hashCode(deterministicator));
        int size = r.nextInt((int) vsrb.calculateMaxSingleElementSize());
        return GenericPerformanceTest.generate_utf8_conform_byte_array(size);
    }

    public static void numTestWRAPPING(BiFunction<VarSizedRingBufferQueueOnly, Integer, byte[]> deterministicGenerator, int num, int max) {
        TransparentBytesStorage store = new ByteArrayStorage(max);
        VarSizedRingBufferQueueOnly vsrb = new VarSizedRingBufferQueueOnly(store, max);

        System.out.println("\n\n\nmax("+max+"), num("+num+")");
        try {
            for (int i = 0; i < num; i++) {
                byte[] e = deterministicGenerator.apply(vsrb, i);
//                System.out.println("generated("+i+") = " + new String(e));
                boolean couldAdd = vsrb.append(e);
                if(!couldAdd)
                    throw new IllegalArgumentException("deterministicGenerator generated an element too large: (num("+num+"), max("+max+"), e.length("+e.length+"))");

//                System.out.println("added("+i+") = " + new String(e));
//                VSBRDebugPrint.printMemoryLayout(vsrb, store, String::new);
                assertFalse(vsrb.isEmpty());

                List<byte[]> iterator = vsrb.iterator().collect();
//                System.out.println("iterator = " + iterator.stream().map(String::new).collect(Collectors.toList()));
                for (int ii = 0; ii < iterator.size(); ii++) {
                    byte[] gen = deterministicGenerator.apply(vsrb, i - ii);
                    byte[] got = iterator.get(iterator.size() - (ii + 1));
                    try {
                        assertArrayEquals(gen, got);
                    } catch (Throwable t) {
                        System.out.println("i = " + i);
                        System.out.println("ii = " + ii);
                        System.out.println("iterator = " + iterator.stream().map(String::new).collect(Collectors.toList()));
                        System.out.println("gen("+(i - ii)+") = " + new String(gen));
                        System.out.println("got("+(iterator.size() - (ii + 1))+") = " + new String(got));
                        VSBRDebugPrint.printContents("After array equality fail ", vsrb, store, String::new);
                        throw t;
                    }
                }
            }
        } finally {
//            VSBRDebugPrint.printContents(vsrb, store, String::new);
            System.out.println("vsrb.size() = " + vsrb.size());
        }
    }
}
