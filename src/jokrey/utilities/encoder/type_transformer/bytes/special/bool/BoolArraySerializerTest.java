package jokrey.utilities.encoder.type_transformer.bytes.special.bool;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Test for BoolArraySerializer.
 * @author jokrey
 */
public class BoolArraySerializerTest {
    @Test
    public void minimal_boolean_array_encoding() {
        boolean[][] os = {
                {},{true},{false},{true, false},{true, true, true},{false, true, false, true},{false, false, false},
                {false, false, false, false, false, false, false, false},{true, true, true, true, true, true, true, true},
                {false, false, true, false, true, false, true, false},{true, true, false, false, false, false, true, true},
                {true, false, true, false, true, false, true, false, false, true},
                {false, true, false, false, false, false, false, false, true, true, true, true, true, true, true, true},
                {true, false, true, false, true, false, true, false, true, true, false, false, false, false, true, true,
                        false, false, true, false, true, true, true, false, true, true, false, false, false, true, true, true,
                        false, true, true, false, true, false, true, false, true, true, false, false, false, false, true, true,
                        true, false, true, false, true, false, true, false, true, true, false, true, false, false, true, true,
                        false, false, true, false, true, false, true, false, true, true, false, false, false, false, true, true,
                        false, true, true, false, true, true, true, false, true, true, false, false, false, false, true, true,
                        false, false, true, false, true, false, true, false, true, true, false, false, false, false, true, true,
                        true, false, true, false, true, false, true, false, true, true, false, true, false, false, true, true,
                        false, false, true, false, true, false, true, false, true, true, false, true, false, false, true, true,
                        false, false, true, false, true, false, true, false, true, true, false, false, false, true, true, true,
                        false, false, true, true, true, false, true, false, true, true, false, false, false, false, true, true,
                        true, false, true, false, true, false, true, false, true, true, false, false, true, false, true, true,
                        false, false, true, false, true, false, true, false, true, true, true, false, false, false, true, true,
                        false, false, true, false, true, false, true, false, true, true, false, false, false, false, true, true},
        };

        for (boolean[] o : os) {
            //            System.out.println(i+" o bits: "+Arrays.toString(o));
            byte[] t = BoolArraySerializer.transform(o);
//            System.out.println(i+" t bytes: "+Arrays.toString(t));
//            System.out.println(i+" t bits: "+Arrays.toString(BitHelper.getBits(t)));
            boolean[] d = BoolArraySerializer.detransform(t);
//            System.out.println(i+" d bits: "+Arrays.toString(d));
            assertArrayEquals(o, d);
//            System.out.println();
        }
    }
}