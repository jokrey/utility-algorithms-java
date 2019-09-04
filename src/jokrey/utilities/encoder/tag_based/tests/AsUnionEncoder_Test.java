package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.debug_analysis_helper.AverageCallTimeMarker;
import jokrey.utilities.encoder.as_union.AsUnionEncoder;
import jokrey.utilities.encoder.as_union.Position;
import jokrey.utilities.encoder.as_union.lb.bytes.LBLIbae;
import jokrey.utilities.encoder.as_union.lb.bytes.LBLIbae_cache;
import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;
import org.junit.Test;

import java.util.Random;

import static jokrey.utilities.encoder.as_union.li.bytes.LIbae.getIntFromByteArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author jokrey
 */
public class AsUnionEncoder_Test {
//    @Test
//    public void test() {
//        for(int i=1;i<10;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=200;i<210;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=300;i<310;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=3000;i<3010;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=30000;i<30010;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=300000;i<300010;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=3000000;i<3000010;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=30000000;i<30000010;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        for(int i=300000000;i<300000010;i++) System.out.println(i+" - "+toString(MD_LIbae.generateMarkDeleteFor(i)));
//        System.out.println("0 - "+toString(MD_LIbae.generateMarkDeleteFor(0)));

//        for(long i=16777218;i<Long.MAX_VALUE/2;i++) {
//            byte[] a = MD_LIbae.generateMarkDeleteFor(i);
//            long ind = getIntFromByteArray(Arrays.copyOfRange(a, 1, 1 + -a[0]));
//            if(i != ind+a.length)
//                System.out.println(i+" - "+printStr(a));
//            assertEquals(i, ind + a.length);
//        }

//        for(long i=72057594037927527L;i<Long.MAX_VALUE;i++) {
//            byte[] a = MD_LIbae.generateMarkDeleteFor(i);
//            long ind = getIntFromByteArray(Arrays.copyOfRange(a, 1, a.length));
//            if(i != ind+a.length)
//                System.out.println(i+" - "+printStr(a));
//            assertEquals(i, ind + a.length);
//        }
//        for(long i=2;i<Long.MAX_VALUE/2;i*=2) {
//            byte[] a = MD_LIbae.generateMarkDeleteFor(i);
//            long ind = getIntFromByteArray(Arrays.copyOfRange(a, 1, a.length));
//            System.out.println(i+" - "+printStr(a));
//            assertEquals(i, ind + a.length);
//        }
//    }

    public static String printStr(byte[] a) {
//        return "["+a[0]+", "+ Arrays.toString(Arrays.copyOfRange(a, 1, a.length)) +" ("+getIntFromByteArray(Arrays.copyOfRange(a, 1, a.length))+")]";
        return "["+ toString(a) +" ("+getIntFromByteArray(a, 1, a.length-1)+")]";
    }
    public static String toString(byte[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]<0&&i!=0?256+a[i]:a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
    @Test
    public void testLBlie_RAM() {
        LBLIbae lie = new LBLIbae(new ByteArrayStorage());
        genericTest(lie, true);
    }
//    @Test
//    public void testLBlieCACHE_RAM() {
//        LBLIbae_cache lie = new LBLIbae_cache(new ByteArrayStorage());
//        genericTest(lie, true);
//    }
//    @Test
//    public void testMDlie_RAM() {
//        MD_LIbae lie = new MD_LIbae(new ByteArrayStorage());
//        genericTest(lie, false);
//    }
//    @Test
//    public void testBMDlie_RAM() {
//        BMD_LIbae lie = new BMD_LIbae(1024, new ByteArrayStorage());
//        genericTest(lie, false);
//    }
    @Test
    public void testLIe_RAM() {
        LIbae lie = new LIbae(new ByteArrayStorage());
        genericTest(lie, true);
    }
//    @Test
//    public void testLBlie_FILE() throws FileNotFoundException {
//        LBLIbae_cached lie = new LBLIbae_cached(new FileStorage(new File(System.getProperty("user.home")+"/Desktop/lblie_file.lblie"), LBLIbae_cached.BLOCK_SIZE));
//        genericTest(lie);
//    }
//    @Test
//    public void testMDlie_FILE() throws FileNotFoundException {
//        MD_LIbae lie = new MD_LIbae(new FileStorage(new File(System.getProperty("user.home")+"/Desktop/mdlie_file.mdlie")));
//        genericTest(lie);
//    }
//    @Test
//    public void testLIe_FILE() throws FileNotFoundException {
//        LIbae lie = new LIbae(new FileStorage(new File(System.getProperty("user.home")+"/Desktop/lie_file.lie")));
//        genericTest(lie);
//    }
//
//
//    @Test
//    public void testLBLIe_FILE_good() throws FileNotFoundException {
//        LBLIbae_cached lblie = new LBLIbae_cached(new FileStorage(new File(System.getProperty("user.home")+"/Desktop/lblie_file.lie")));
//        lblie.clear();
//        genericTest_encodeDecodeDelete_SameSize(lblie, 2000, 1000000);
//        AverageCallTimeMarker.print_all("1 ready");
//        LIbae lie = new LIbae(new FileStorage(new File(System.getProperty("user.home")+"/Desktop/lie_file.lie")));
//        lie.clear();
//        genericTest_encodeDecodeDelete_SameSize(lie, 2000, 1000000);
//        AverageCallTimeMarker.print_all("compare 1");
//
//        lblie.clear();
//        lie.clear();
//    }


    @Test
    public void testIntFromByteArray() {
        assertEquals(200, getIntFromByteArray(new byte[]{-56}, 0, 1));
    }











    public static byte[] randBytes(int size) {
        byte[] gen = new byte[size];
        new Random().nextBytes(gen);
        return gen;
    }

    public static<Pos extends Position> void genericTest(AsUnionEncoder<byte[], Pos> encoder, boolean assumeEncodeMeansAppend) {
        genericTest_cornerCases(encoder);

        if(assumeEncodeMeansAppend) {
            genericTest_encodeDecode(encoder, 100, 1);
    //        genericTest_encodeDecode(encoder, 5000, 1);
            genericTest_encodeDecode(encoder, 100, 200);
            genericTest_encodeDecode(encoder, 1000, 100);
        }

        genericTest_encodeDecodeDelete_IncreaseSize(encoder, 2, 1);
//        genericTest_encodeDecodeDelete_IncreaseSize(encoder, 5000, 1);
        genericTest_encodeDecodeDelete_IncreaseSize(encoder, 10, 1);
        genericTest_encodeDecodeDelete_IncreaseSize(encoder, 1000, 100);
        genericTest_encodeDecodeDelete_IncreaseSize(encoder, 20, 10000);

        genericTest_encodeDecodeDelete_DecreaseSize(encoder, 100, 1);
//        genericTest_encodeDecodeDelete_DecreaseSize(encoder, 5000, 1);
        genericTest_encodeDecodeDelete_DecreaseSize(encoder, 100, 200);
        genericTest_encodeDecodeDelete_DecreaseSize(encoder, 1000, 100);
        genericTest_encodeDecodeDelete_DecreaseSize(encoder, 20, 10000);

        genericTest_encodeDecodeDelete_SameSize(encoder, 100, 10000);

        if(assumeEncodeMeansAppend) {
            genericTest_encodeDecodeSkip(encoder, 100, 1);
    //        genericTest_encodeDecodeSkip(encoder, 5000, 1);
            genericTest_encodeDecodeSkip(encoder, 100, 200);
            genericTest_encodeDecodeSkip(encoder, 1000, 100);
        }

        AverageCallTimeMarker.print_all(encoder.getClass().getName());
    }








    public static<Pos extends Position> void genericTest_encodeDecode(AsUnionEncoder<byte[], Pos> encoder, int elementCount, int dataMultiplier) {
//        MD_LIbae.print(encoder);
        encoder.clear();
//        MD_LIbae.print(encoder);

        AverageCallTimeMarker.mark_call_start("genericTest_encodeDecode("+elementCount+", "+dataMultiplier+")");
        byte[][] testData = new byte[elementCount][];
        for(int i=0;i<testData.length;i++) {
            testData[i] = randBytes(i*dataMultiplier);
            System.out.println("testData["+i+"].length = " + testData[i].length);
            encoder.encode(testData[i]);
//            if(encoder instanceof LBLIbae_cache) ((LBLIbae_cache)encoder).debugPrintAllBlocks();
//            MD_LIbae.print(encoder);
            assertArrayEquals(testData[i], encoder.decode(encoder.forIndex(i)));
        }

        for(int i=0;i<testData.length;i++) {
            assertArrayEquals(testData[i], encoder.decode(encoder.forIndex(i)));
        }

        Pos pos = encoder.reset();
        Pos pos2 = encoder.reset();
        for (byte[] aTestData : testData) {
            assertArrayEquals(aTestData, encoder.decode(pos));
            assertEquals(aTestData.length, encoder.skipEntry(pos2));
        }
        AverageCallTimeMarker.mark_call_end("genericTest_encodeDecode("+elementCount+", "+dataMultiplier+")");
    }

    public static<Pos extends Position> void genericTest_encodeDecodeDelete_SameSize(AsUnionEncoder<byte[], Pos> encoder, int elementCount, int dataSize) {
        encoder.clear();

        AverageCallTimeMarker.mark_call_start(encoder.getClass().getName()+" - genericTest_encodeDecodeDelete_SameSize("+elementCount+", "+dataSize+")");
        for(int i=elementCount-1;i>=0;i--) {
            byte[] gen = randBytes(dataSize);
            encoder.encode(gen);
            byte[] decoded = encoder.decode(encoder.reset());
            long bytesDeleted = encoder.delete(encoder.reset());
            assertArrayEquals(gen, decoded);
            assertEquals(gen.length, bytesDeleted);
        }
        System.out.println("between(dec); "+encoder.getRawSize());
        for(int i=elementCount-1;i>=0;i--) {
            byte[] gen = randBytes(dataSize);
            encoder.encode(gen);
            byte[] decoded = encoder.decode(encoder.reset());
            byte[] deleted = encoder.deleteEntry(encoder.reset());
            assertArrayEquals(gen, decoded);
            assertArrayEquals(gen, deleted);
        }
        AverageCallTimeMarker.mark_call_end(encoder.getClass().getName()+" - genericTest_encodeDecodeDelete_SameSize("+elementCount+", "+dataSize+")");
    }

    public static<Pos extends Position> void genericTest_encodeDecodeDelete_DecreaseSize(AsUnionEncoder<byte[], Pos> encoder, int elementCount, int dataMultiplier) {
        encoder.clear();

        AverageCallTimeMarker.mark_call_start("genericTest_encodeDecodeDelete_DecreaseSize("+elementCount+", "+dataMultiplier+")");
        for(int i=elementCount-1;i>=0;i--) {
            byte[] gen = randBytes(i*dataMultiplier);
            encoder.encode(gen);
            byte[] decoded = encoder.decode(encoder.reset());
            long bytesDeleted = encoder.delete(encoder.reset());
            assertArrayEquals(gen, decoded);
            assertEquals(gen.length, bytesDeleted);
        }
        System.out.println("between(dec); "+encoder.getRawSize());
        for(int i=elementCount-1;i>=0;i--) {
            byte[] gen = randBytes(i*dataMultiplier);
            encoder.encode(gen);
            byte[] decoded = encoder.decode(encoder.reset());
            byte[] deleted = encoder.deleteEntry(encoder.reset());
            assertArrayEquals(gen, decoded);
            assertArrayEquals(gen, deleted);
        }
        AverageCallTimeMarker.mark_call_end("genericTest_encodeDecodeDelete_DecreaseSize("+elementCount+", "+dataMultiplier+")");
    }

    public static<Pos extends Position> void genericTest_encodeDecodeDelete_IncreaseSize(AsUnionEncoder<byte[], Pos> encoder, int elementCount, int dataMultiplier) {
        encoder.clear();

        AverageCallTimeMarker.mark_call_start("genericTest_encodeDecodeDelete_IncreaseSize("+elementCount+", "+dataMultiplier+")");
        for(int i=0;i<elementCount;i++) {
//            System.out.println("\ni: "+ i);
            byte[] gen = randBytes(i*dataMultiplier);
//            System.out.println("gen: "+ Arrays.toString(gen));
//            System.out.println("Arrays.toString(encoder.getEncoded()) = " + Arrays.toString(encoder.getEncoded()));
            encoder.encode(gen);
//            System.out.println("Arrays.toString(encoder.getEncoded()) = " + Arrays.toString(encoder.getEncoded()));
            byte[] decoded = encoder.decode(encoder.reset());
            long bytesDeleted = encoder.delete(encoder.reset());
//            System.out.println("Arrays.toString(encoder.getEncoded()) = " + Arrays.toString(encoder.getEncoded()));
            assertArrayEquals(gen, decoded);
            assertEquals(gen.length, bytesDeleted);
        }
        System.out.println("between(inc); "+encoder.getRawSize());
        for(int i=0;i<elementCount;i++) {
//            System.out.println("\ni: "+ i);
            byte[] gen = randBytes(i*dataMultiplier);
//            System.out.println("gen: "+ Arrays.toString(gen));
//            System.out.println("Arrays.toString(encoder.getEncoded()) = " + Arrays.toString(encoder.getEncoded()));
            encoder.encode(gen);
//            System.out.println("Arrays.toString(encoder.getEncoded()) = " + Arrays.toString(encoder.getEncoded()));
            byte[] decoded = encoder.decode(encoder.reset());
            byte[] deleted = encoder.deleteEntry(encoder.reset());
//            System.out.println("Arrays.toString(encoder.getEncoded()) = " + Arrays.toString(encoder.getEncoded()));
            assertArrayEquals(gen, decoded);
            assertArrayEquals(gen, deleted);
        }
        AverageCallTimeMarker.mark_call_end("genericTest_encodeDecodeDelete_IncreaseSize("+elementCount+", "+dataMultiplier+")");
    }

    public static<Pos extends Position> void genericTest_encodeDecodeSkip(AsUnionEncoder<byte[], Pos> encoder, int elementCount, int dataMultiplier) {
        encoder.clear();

        AverageCallTimeMarker.mark_call_start("genericTest_encodeDecodeSkip("+elementCount+", "+dataMultiplier+")");
        Pos pos = encoder.reset();
        Pos pos2 = encoder.reset();
        for(int i=0;i<elementCount;i++) {
            byte[] gen = randBytes(i*dataMultiplier);
            encoder.encode(gen);
            byte[] decoded = encoder.decode(pos);
            long bytesSkipped = encoder.skipEntry(pos2);
            assertEquals(gen.length, bytesSkipped);
            assertArrayEquals(gen, decoded);
        }
        AverageCallTimeMarker.mark_call_end("genericTest_encodeDecodeSkip("+elementCount+", "+dataMultiplier+")");
    }

    public static<Pos extends Position> void genericTest_cornerCases(AsUnionEncoder<byte[], Pos> encoder) {
//        MD_LIbae.print(encoder);
        encoder.clear();

        byte[] arr = randBytes(1200);
        encoder.encode(arr);
//        if(encoder instanceof LBLIbae_cache) ((LBLIbae_cache)encoder).debugPrintAllBlocks();
        assertArrayEquals(arr, encoder.decode(encoder.forIndex(0)));

        Pos pos = encoder.reset();
        AverageCallTimeMarker.mark_call_start("genericTest_cornerCases()");
        encoder.encode(randBytes(500));
        encoder.encode(randBytes(400));
        encoder.skip(pos);
//        if(encoder instanceof LBLIbae_cache) ((LBLIbae_cache)encoder).debugPrintAllBlocks();
        encoder.delete(pos);
//        if(encoder instanceof LBLIbae_cache)
//            ((LBLIbae_cache)encoder).debugPrintAllBlocks();
        encoder.encode(randBytes(600));
//        if(encoder instanceof LBLIbae_cache)
//            ((LBLIbae_cache)encoder).debugPrintAllBlocks();
        AverageCallTimeMarker.mark_call_end("genericTest_cornerCases()");
    }
}
