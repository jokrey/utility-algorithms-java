package jokrey.utilities.encoder.type_transformer;

import jokrey.utilities.encoder.tag_based.implementation.length_indicator.type.transformer.LITypeToBytesTransformer;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.type.transformer.LITypeToStringTransformer;
import jokrey.utilities.encoder.type_transformer.TypeToFromRawTransformer;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for both string and byte[] transformers
 * @author jokrey
 */
public class TransformerTest {
    @Test
    public void test_bytes_transformer() {
        test_transformer(new LITypeToBytesTransformer());
    }
    @Test
    public void test_string_transformer() {
        test_transformer(new LITypeToStringTransformer());
    }

    private <SF> void test_transformer(TypeToFromRawTransformer<SF> transformer) {
        boolean[] o1 = new boolean[] {true, false, false, true, false, true, true, true, true};
        assertArrayEquals(o1, transformer.detransform(transformer.transform(o1), o1.getClass()));
        byte[] o2 = new byte[] {1,2,3,4,5,6,7};
        assertArrayEquals(o2, transformer.detransform(transformer.transform(o2), o2.getClass()));
        short[] o3 = new short[] {1,2,3,4,5,6,7};
        assertArrayEquals(o3, transformer.detransform(transformer.transform(o3), o3.getClass()));
        int[] o4 = new int[] {1,2,3,4,5,6,7};
        assertArrayEquals(o4, transformer.detransform(transformer.transform(o4), o4.getClass()));
        long[] o5 = new long[] {1,2,3,4,5,6,7};
        assertArrayEquals(o5, transformer.detransform(transformer.transform(o5), o5.getClass()));
        float[] o6 = new float[] {1,2,3,4,5,6,7};
        assertArrayEquals(o6, transformer.detransform(transformer.transform(o6), o6.getClass()), 0f);
        double[] o7 = new double[] {1,2,3,4,5,6,7};
        assertArrayEquals(o7, transformer.detransform(transformer.transform(o7), o7.getClass()), 0d);
        char[] o8 = new char[] {'a', 'x', '?', 'ä', 'í', '1'};
        assertArrayEquals(o8, transformer.detransform(transformer.transform(o8), o8.getClass()));

        boolean p1 = true;
        assertEquals(p1, transformer.detransform(transformer.transform(p1), boolean.class));
        assertEquals(p1, transformer.detransform(transformer.transform(p1), Boolean.class));
        byte p2 = 13;
        assertEquals(new Byte(p2), transformer.detransform(transformer.transform(p2), byte.class));
        assertEquals(new Byte(p2), transformer.detransform(transformer.transform(p2), Byte.class));
        short p3 = 13000;
        assertEquals(new Short(p3), transformer.detransform(transformer.transform(p3), short.class));
        assertEquals(new Short(p3), transformer.detransform(transformer.transform(p3), Short.class));
        int p4 = 356234513;
        assertEquals(new Integer(p4), transformer.detransform(transformer.transform(p4), int.class));
        assertEquals(new Integer(p4), transformer.detransform(transformer.transform(p4), Integer.class));
        long p5 = 9924382344534513L;
        assertEquals(new Long(p5), transformer.detransform(transformer.transform(p5), long.class));
        assertEquals(new Long(p5), transformer.detransform(transformer.transform(p5), Long.class));
        float p6 = 133242534675657.123123123f;
        assertEquals(new Float(p6), transformer.detransform(transformer.transform(p6), float.class));
        assertEquals(new Float(p6), transformer.detransform(transformer.transform(p6), Float.class));
        double p7 = 9865756756756756756753713.213123523234d;
        assertEquals(new Double(p7), transformer.detransform(transformer.transform(p7), double.class));
        assertEquals(new Double(p7), transformer.detransform(transformer.transform(p7), Double.class));
        char p8 = 'ó';
        assertEquals(new Character(p8), transformer.detransform(transformer.transform(p8), char.class));
        assertEquals(new Character(p8), transformer.detransform(transformer.transform(p8), Character.class));
        String p9 = "asfd lakzrn34vz3vzg874zvgae4b 7bzg8osez g74zgeagh847hse i hgseuhv784hv";
        assertEquals(p9, transformer.detransform(transformer.transform(p9), p9.getClass()));

        //recursively supported arrays
        String[] a1 = {p9, "213123", "ä+sdäf+sdäf#+däsf+äsdvf", "test", ""};
        assertArrayEquals(a1, transformer.detransform(transformer.transform(a1), a1.getClass()));

        String[][] a2 = {{p9, "213123", "ä+sdäf+sdäf#+däsf+äsdvf", "test"}, {p9, p9, p9, p9, "1"}, {"9","1","4","1","1","6"}};
        assertArrayEquals(a2, transformer.detransform(transformer.transform(a2), a2.getClass()));

        String[][][] a3 = {{}, a2, {{p9, "213123", "ä+sdäf+sdäf#+däsf+äsdvf", "test"}, {p9, p9, p9, p9, "1"}, {"9","1","4","1","1","6"}}};
        assertArrayEquals(a3, transformer.detransform(transformer.transform(a3), a3.getClass()));

        int[][] a4 = {{}, o4, {1,2,3,4,5,6,7,8,9,0,2323234}, o4, o4, {1,9865333}, o4};
        assertArrayEquals(a4, transformer.detransform(transformer.transform(a4), a4.getClass()));

        assertTrue(transformer.canTransform(byte.class));
        assertTrue(transformer.canTransform(byte[].class));
        assertTrue(transformer.canTransform(String[][].class));
    }
}
