package jokrey.utilities.encoder.as_union.tests;

import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class LIbaeTest {
    @Test
    public void generateLITest() {
        byte[] libaeBytes;
        libaeBytes = LIbae.generateLI(0);
        assertArrayEquals(new byte[] {0}, libaeBytes);
        libaeBytes = LIbae.generateLI(1);
        assertArrayEquals(new byte[] {1, 1}, libaeBytes);
        libaeBytes = LIbae.generateLI(2);
        assertArrayEquals(new byte[] {1, 2}, libaeBytes);

        checkLIGenerationFor(256);
        checkLIGenerationFor(564);
        for (int i=0;i<123012031;i+=123) {
            checkLIGenerationFor(i);
        }
    }

    public void checkLIGenerationFor(int l) {
        byte[] libaeBytes = LIbae.generateLI(l);
        long[] bounds = LIbae.get_next_li_bounds(libaeBytes, 0, 0, (l+1) * 2L);//+1 for 0
        if(bounds == null) {
            System.out.println("l = " + l);
            System.out.println("libaeBytes = " + Arrays.toString(libaeBytes));
        }
        assertNotNull(bounds);
        assertEquals(l, bounds[1]-bounds[0]);
    }



    @Test
    public void generateReverseLITest() {
        byte[] libaeBytes;
        libaeBytes = LIbae.generateReverseLI(0);
        assertArrayEquals(new byte[] {0}, libaeBytes);
        libaeBytes = LIbae.generateReverseLI(1);
        assertArrayEquals(new byte[] {1, 1}, libaeBytes);
        libaeBytes = LIbae.generateReverseLI(2);
        assertArrayEquals(new byte[] {2, 1}, libaeBytes);

        checkReverseLIGenerationFor(256);
        checkReverseLIGenerationFor(564);
        for (int i=0;i<123012031;i+=123) {
            checkReverseLIGenerationFor(i);
        }
    }

    public void checkReverseLIGenerationFor(int l) {
        byte[] libaeBytes = LIbae.generateReverseLI(l);
        long[] bounds = LIbae.get_next_reverse_li_bounds(libaeBytes, libaeBytes.length-1, libaeBytes.length-1, (l+1) * 2L);//+1 for 0
        assertNotNull(bounds);
        assertEquals(l, bounds[1]-bounds[0]);
    }


    @Test
    public void encodeLength0Array() {
        byte[] toEncode = new byte[0];
        LIbae encoder = new LIbae();
        encoder.encode(toEncode);
        assertArrayEquals(toEncode, encoder.decodeFirst());
    }
}
