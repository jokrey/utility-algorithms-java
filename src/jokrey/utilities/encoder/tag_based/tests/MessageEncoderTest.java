package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.as_union.li.bytes.MessageEncoder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author jokrey
 */
public class MessageEncoderTest {
    @Test
    public void iteratorTest() {
        byte[] ba0 = new byte[] {};
        byte[] ba1 = new byte[] {1,2,3,4,5,6};
        boolean bo2 = true;
        byte[] ba3 = new byte[] {14,2,3,4,15,6};
        byte[] ba4 = new byte[] {11,25,3,14,5,16};
        int i5 = 1235622;
        double d6 = 14455464576568567.234234234;
        byte[] ba7 = new byte[] {111,121,31,14,15,16};
        byte by8 = 126;
        float f9 = 18567.2334f;
        short s10 = 21459;
        long l11 = 288587571435786459L;

        MessageEncoder encoder = MessageEncoder.encodeAll(100, ba0, ba1, bo2, ba3, ba4, i5, d6, ba7, by8, f9, s10, l11);

        encoder.resetPointer();
        int counter = 0;
        for (byte[] bs : encoder) {
            if (counter == 0)
                assertArrayEquals(ba0, bs);
            else if(counter == 1) {
                assertArrayEquals(ba1, bs);
                counter++;
                assertEquals(bo2, encoder.nextBool());
            } else if(counter == 3)
                assertArrayEquals(ba3, bs);
            else if(counter == 4) {
                assertArrayEquals(ba4, bs);
                counter++;
                assertEquals(i5, encoder.nextInt());
                counter++;
                assertEquals(d6, encoder.nextDouble(), 0.01);
            } else if(counter == 7) {
                assertArrayEquals(ba7, bs);
                counter++;
                assertEquals(by8, encoder.nextByte());
                counter++;
                assertEquals(f9, encoder.nextFloat(), 0.01);
                counter++;
                assertEquals(s10, encoder.nextShort());
                counter++;
                assertEquals(l11, encoder.nextLong());
            }
            counter++;
        }
        assertEquals(12, counter);
    }


    @Test
    public void fringeCaseTests() {
        byte[] t1 = {};
        byte[] t2 = {1,2};
        MessageEncoder encoder = MessageEncoder.encodeAll(0, t1, t2);
        encoder.resetPointer();

        byte[] t1d = encoder.nextVariable();
        byte[] t2d = encoder.nextVariable();
        byte[] tnulld = encoder.nextVariable();

        assertArrayEquals(t1, t1d);
        assertArrayEquals(t2, t2d);
        assertNull(tnulld);


        byte[] t3 = {};
        encoder = MessageEncoder.encodeAll(0, t1, t2, t3);
        encoder.resetPointer();

        t1d = encoder.nextVariable();
        t2d = encoder.nextVariable();
        byte[] t3d = encoder.nextVariable();

        assertArrayEquals(t1, t1d);
        assertArrayEquals(t2, t2d);
        assertArrayEquals(t3, t3d);
    }
}
