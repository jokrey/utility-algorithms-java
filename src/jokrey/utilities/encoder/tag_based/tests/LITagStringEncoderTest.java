package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.debug_analysis_helper.ConcurrentPoolTester;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.LIe;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.string.LITagStringEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.string.LIse;
import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Testing USE
 *
 * @author jokrey
 */
public class LITagStringEncoderTest {
    @Test
    public void do_lise_test() {
        String[] to_encode = {"test", "arrstart", "1", "2", "", "0", "10", "1234567890"};
        LIse lise = new LIse();
        int i = 0;
        lise.li_encode(to_encode[i++]);
        lise.li_encode(to_encode[i++], to_encode[i++], to_encode[i++]);
        for(;i<to_encode.length;i++)
            lise.li_encode(to_encode[i]);
        assertArrayEquals(to_encode, lise.li_decode_all());
        LIe.Position pos = lise.reset();
        assertArrayEquals(new String[] {"test", "arrstart"}, lise.li_decode(pos, 2));
        assertArrayEquals(new String[] {"1", "2"}, lise.li_decode(pos, 2));
        pos = lise.reset();
        assertArrayEquals(to_encode, lise.li_decode(pos, 500));
        assertArrayEquals(new String[0], lise.li_decode(pos, 0));

        lise = new LIse("");
        assertNull(lise.li_decode_first());
    }

    @Test
    public void do_tag_system_test() {
        //order, values and type don't matter
        //just keep in mind that EACH tag has to have a different name!!!!

        LITagStringEncoder encoder = new LITagStringEncoder();

        TagSystemTestHelper.enter_values(encoder);
        assertEquals(encoder.getEncodedString().length(), encoder.getRawStorageSystem().contentSize());

        String encodedString = encoder.getEncodedString();

        LITagStringEncoder decoder = new LITagStringEncoder(encodedString);
        int length_before = decoder.getEncodedString().length();

        TagSystemTestHelper.do_tag_system_assertions_without_delete(decoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(decoder);

        int length_after = decoder.getEncodedString().length();
        assertTrue(length_after < length_before);     //some values have been deleted, so the string is shorter now...
        assertEquals(length_after, decoder.getRawStorageSystem().contentSize());


        TagSystemTestHelper.basic_typed_system_test(encoder);
    }

    @Test
    public void ase_iterator_test() {
        LITagStringEncoder ase = new LITagStringEncoder();
        ase.addEntryT_nocheck("1", 1);
        ase.addEntryT_nocheck("123", "25");
        ase.addEntryT_nocheck("532", "235");
        ase.addEntryT_nocheck("run", "why?");

        int count = 0;
        for(TagBasedEncoder.TaggedEntry<String> te:ase) {
            if(count == 0) {
                assertEquals(te.tag, "1");
                assertEquals(te.val, "1");
            } else if(count == 1) {
                assertEquals(te.tag, "123");
                assertEquals(te.val, "25");
            } else if(count == 2) {
                assertEquals(te.tag, "532");
                assertEquals(te.val, "235");
            } else if(count == 3) {
                assertEquals(te.tag, "run");
                assertEquals(te.val, "why?");
            }

            //confusing the algorithm is not simple proof
            ase.getEntry("1");
            ase.getEntry("run");

            //nestability proof
            int inner_counter = 0;
            for(TagBasedEncoder.TaggedEntry<String> te_innner:ase) {
                if(inner_counter == 0) {
                    assertEquals(te_innner.tag, "1");
                    assertEquals(te_innner.val, "1");
                } else if(inner_counter == 1) {
                    assertEquals(te_innner.tag, "123");
                    assertEquals(te_innner.val, "25");
                } else if(inner_counter == 2) {
                    assertEquals(te_innner.tag, "532");
                    assertEquals(te_innner.val, "235");
                } else if(inner_counter == 3) {
                    assertEquals(te_innner.tag, "run");
                    assertEquals(te_innner.val, "why?");
                }
                inner_counter++;
            }
            assertEquals(inner_counter, ase.getTags().length);

            count++;
        }

        assertEquals(count, ase.getTags().length);
    }

    @Test
    public void lise_iterator_test() {
        LIse lise = new LIse();
        LIe.Position pos = lise.reset();
        String[] values = {"wersadfa", "23234234", "324ads213f", "§§$!§%&&/&GBFÄBÖ", "324ads213f", "§§$!§%&&/&GBFÄBÖ"};
        lise.li_encode(values);

        int count = 0;
        for(String s:lise) {
            assertEquals(values[count], s);

            //confusing the algorithm is not simple proof
            lise.li_decode(pos);
            lise.li_skip(pos);

            //nestability proof
            int inner_counter = 0;
            for(String s_inner:lise) {
                assertEquals(values[inner_counter], s_inner);
                inner_counter++;
            }
            assertEquals(inner_counter, lise.li_decode_all().length);

            count++;
        }

        assertEquals(count, lise.li_decode_all().length);
    }


    @Test
    public void lise_not_concurrent_read_write_test() throws Throwable {
        String tag = "1221ääüö";
        StringBuilder val = new StringBuilder();
        for(int i=0;i<1000;i++)
            val.append(TagSystemTestHelper.getRandomNr(0, 255));


        LIse lise = new LIse();
        concurrent_write_to(tag, val.toString(), lise, true);

        try {
            Iterator<String> iter = lise.iterator();
            while (iter.hasNext()) {
                assertEquals(tag, iter.next());
                assertEquals(val.toString(), iter.next());
            }
            fail("really? this worked by chance?? Very unlikely.");
        } catch(Exception | AssertionError ex) {
            //supposed to happen
        }
    }

    private void concurrent_write_to(String tag, String val, LIse lise, boolean exception_expected) throws Throwable {
        Runnable r1_write = () -> {
            try {
                lise.li_encode(tag, val);
            } catch(Exception e) {
                if(!exception_expected)
                    e.printStackTrace();
            }
        };

        int n_threads = 100;
        ConcurrentPoolTester executor = new ConcurrentPoolTester(n_threads);
        for(int i = 0; i < n_threads; i++)
            executor.execute(r1_write);

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        if(!exception_expected)
            executor.throwLatestException();
    }
}