package jokrey.utilities.date_time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * simple test of the encoding of the ExactDateTime class.
 */
public class EDTEncodingTest {
    @Test
    public void encoding_test() {
        ExactDateTime dt = new ExactDateTime(new ExactDate(29, 2, 2020), new ExactTimeOfDay(12, 59, 12, 32));
//        System.out.println(dt.toString());
        String encoded = dt.getEncodedString();
//        System.out.println(encoded);
        ExactDateTime decoded_dt = new ExactDateTime(encoded);
//        System.out.println(decoded_dt.toString());
        assertEquals(dt, decoded_dt);
    }
}
