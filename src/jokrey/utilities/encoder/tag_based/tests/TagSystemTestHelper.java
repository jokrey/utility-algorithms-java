package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.TagBasedEncoderBytes;
import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.encoder.as_union.li.string.LIse;
import jokrey.utilities.encoder.tag_based.tuple_tag.TupleTagBasedEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *
 * @author jokrey
 */
public class TagSystemTestHelper {
    //TEST VALUES
    public static final String bool_test_tag = "false";
    public static final boolean bool_test_value = true;
    public static final String str_test_tag = "+";
    public static final String str_test_value = "ThisIsWhat?A STRING?==???????";
    public static final String empty_tag_test_tag = "";
    public static final String empty_tag_test_value = "EmptyWorksAlso??????????";
    public static final String empty_val_test_tag = "TEST";
    public static final String empty_val_test_value = "";
    public static final String int_test_tag = "12";
    public static final int int_test_value = 47845721;
    public static final String weird_long_tag = "214ohH(/G()\"B!$)B1^  b*#+#+#**'*8736284765/&$&§&%\"$!§/(!%$/(%§&$/%§\"/)&$\"§(/&%(=\"§&%=\"§&=\"§%&=(/\"§%(\"/§%&\")))))    \n\n 12312";
    public static final String weird_long_value = "214ohH(/G()\"B!$)B1^b*#+#+#**'*   8736284765/&$&§&%\"$!§/(!%$/(%§&$/%§\"/)&$\"§(/&%(=\"§&%=\"§&=\"§%&=(/\"§%(\"/§%&\")))))     \n\n\n     1232";

    public static final String empty_strarr_test_tag = "empty_strarr";
    public static final String[] empty_strarr_test_value = {};
    public static final String strarr_test_tag = "nice_strarr";
    public static final String[] strarr_test_value = {"test", "wkfdjaskdakjsdjaklsdj", "test"};
    public static final String strarr2d_test_tag = "nice_strarr2d";
    public static final String[][] strarr2d_test_value = {
            {"teasdast", "wkfdjaskdakjsdjasdasdasdaklsdj", "323test", "xxy"},
            {"tes22t", "wqeadsadsas", "te111st", "te111st", "te111st", "te111st"},
            {"1", "2", "3", "4", "5", "6", "6", "6", "6", "6", "6", "6", "6", "6"}};

    public static final String empty_intarr_test_tag = "empty_intarr";
    public static final int[] empty_intarr_test_value = {};
    public static final String intarr_test_tag = "nice_intarr";
    public static final int[] intarr_test_value = {12314123, 7452, 881,1121};



    public static void enter_values(TagBasedEncoder<?> encoder) {
        //some double adds that should not matter
        encoder.addEntryT(bool_test_tag, bool_test_value);
        encoder.addEntryT("confuse???123123", 1234123+"WeirdTag ValueSwear");
        encoder.addEntryT(str_test_tag, str_test_value);
        encoder.addEntryT("confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT("confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(int_test_tag,  int_test_value);
        encoder.addEntryT("WeirdTag confuse", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT("confuse???", 1234123+"WeirdTagVa lueSwear");
        encoder.addEntryT(weird_long_tag, weird_long_value);
        encoder.addEntryT(empty_tag_test_tag, empty_tag_test_value);
        encoder.addEntryT(empty_val_test_tag, empty_val_test_value);
        encoder.addEntryT("confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(empty_strarr_test_tag, empty_strarr_test_value);  //empty is not written into the system.
        encoder.addEntryT(strarr_test_tag, strarr_test_value);
        encoder.addEntryT(empty_intarr_test_tag, empty_intarr_test_value);  //empty is not written into the system.
        encoder.addEntryT(intarr_test_tag, intarr_test_value);
        encoder.addEntryT("confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(strarr2d_test_tag, strarr2d_test_value);
        encoder.addEntryT("confuse???1231235", 1234123+"WeirdTa gValueSwear");
    }

    public static void do_tag_system_assertions_without_delete(TagBasedEncoder<?> decoder) {
        assertTrue(decoder.exists(str_test_tag));
//        assertTrue(str_test_value.getBytes(StandardCharsets.UTF_8).length <= decoder.length(str_test_tag)); //cannot be generally asserted, because certain encoders may change the length
        assertEquals(str_test_value, decoder.getEntryT(str_test_tag, String.class));
        assertEquals(weird_long_value, decoder.getEntryT(weird_long_tag, String.class));   //because the tag was deleted before
        assertEquals(bool_test_value, decoder.getEntryT(bool_test_tag, boolean.class));
        assertEquals(empty_tag_test_value, decoder.getEntryT(empty_tag_test_tag, String.class));
        assertArrayEquals(empty_strarr_test_value, decoder.getEntryT(empty_strarr_test_tag, String[].class));
        assertArrayEquals(strarr_test_value, decoder.getEntryT(strarr_test_tag, String[].class));
        assertArrayEquals(empty_intarr_test_value, decoder.getEntryT(empty_intarr_test_tag, int[].class));
        assertArrayEquals(intarr_test_value, decoder.getEntryT(intarr_test_tag, int[].class));
        assertTrue(Arrays.deepEquals(strarr2d_test_value, decoder.getEntryT(strarr2d_test_tag, String[][].class)));
        assertEquals(int_test_value, decoder.getEntryT(int_test_tag, int.class).intValue());
//        assertEquals(decoder.getTags()[0], bool_test_tag);  //may be wrong if tester added other stuff
    }

    public static void do_tag_system_assertions_delete(TagBasedEncoder<?> decoder) {
        assertEquals(empty_tag_test_value, decoder.deleteEntryT(empty_tag_test_tag, String.class));
        assertArrayEquals(empty_strarr_test_value, decoder.deleteEntryT(empty_strarr_test_tag, String[].class));
        assertNull(decoder.deleteEntryT(empty_strarr_test_tag, String[].class));
        assertArrayEquals(strarr_test_value, decoder.deleteEntryT(strarr_test_tag, String[].class));
        assertNull(decoder.getEntryT(empty_tag_test_tag, String.class));
        assertNull(decoder.getEntryT(strarr_test_tag, String[].class));
    }

    /**
     * Tests for most(except infinite variations on arrays) available, supported data types whether or not it can correctly recreate the stored object.
     * This functionality is required by the serializer
     */
    public static void basic_typed_system_test(TagBasedEncoder<?> tbe) {
        Object orig_1 = "whatup";
        Object orig_2 = false;
        Object orig_3 = (byte) 123;
        Object orig_4 = 65432;
        Object orig_5 = 45645674655L;
        Object orig_6 = true;
        Object orig_7 = (byte)-23;
        Object orig_8 = -6123123;
        Object orig_9 = -32472532423245L;
        Object orig_10 = new String[] {"123123141", "234234234", "gsdkjlhgsjdfsjlg", "test"};
        Object orig_11 = new boolean[]{false, true, true, false};
        Object orig_12 = new byte[]{-12, 13, 15, 93, -80};
        Object orig_13 = new int[]{121321, -123123, 123123, 123123, -14123, 876574};
        Object orig_14 = new long[]{-32472213123423245L, -32472212423245L, -32472532423242L};
        Object orig_15 = new String[][]{{"test", "test", "test"}, {"1", "2", "3"}};
        Object orig_16 = new LIbae();
        Object orig_17 = new LIse("hallo", "dies", "ist", "ein", "test");

        tbe.addEntryT("#####1", orig_1);
        tbe.addEntryT("#####2", orig_2);
        tbe.addEntryT("#####3", orig_3);
        tbe.addEntryT("#####4", orig_4);
        tbe.addEntryT("#####5", orig_5);
        tbe.addEntryT("#####6", orig_6);
        tbe.addEntryT("#####7", orig_7);
        tbe.addEntryT("#####8", orig_8);
        tbe.addEntryT("#####9", orig_9);
        tbe.addEntryT("#####10", orig_10);
        tbe.addEntryT("#####11", orig_11);
        tbe.addEntryT("#####12", orig_12);
        tbe.addEntryT("#####13", orig_13);
        tbe.addEntryT("#####14", orig_14);
        tbe.addEntryT("#####15", orig_15);
        tbe.addEntryT("#####16", orig_16);
        tbe.addEntryT("#####17", orig_17);

        String result_1 = tbe.getEntryT("#####1", String.class);
        Boolean result_2 = tbe.getEntryT("#####2", Boolean.class);
        Byte result_3 = tbe.getEntryT("#####3", Byte.class);
        Integer result_4 = tbe.getEntryT("#####4", Integer.class);
        Long result_5 = tbe.getEntryT("#####5", Long.class);
        boolean result_6 = tbe.getEntryT("#####6", boolean.class);
        byte result_7 = tbe.getEntryT("#####7", byte.class);
        int result_8 = tbe.getEntryT("#####8", int.class);
        long result_9 = tbe.getEntryT("#####9", long.class);
        String[] result_10 = tbe.getEntryT("#####10", String[].class);
        boolean[] result_11 = tbe.getEntryT("#####11", boolean[].class);
        byte[] result_12 = tbe.getEntryT("#####12", byte[].class);
        int[] result_13 = tbe.getEntryT("#####13", int[].class);
        long[] result_14 = tbe.getEntryT("#####14", long[].class);
        String[][] result_15 = tbe.getEntryT("#####15", String[][].class);
        LIbae result_16 = tbe.getEntryT("#####16", LIbae.class);
        LIse result_17 = tbe.getEntryT("#####17", LIse.class);

        assertEquals(orig_1, result_1);
        assertEquals(orig_2, result_2);
        assertEquals(orig_3, result_3);
        assertEquals(orig_4, result_4);
        assertEquals(orig_5, result_5);
        assertEquals(orig_6, result_6);
        assertEquals(orig_7, result_7);
        assertEquals(orig_8, result_8);
        assertEquals(orig_9, result_9);
        assertArrayEquals((String[])orig_10, result_10);
        assertArrayEquals((boolean[]) orig_11, result_11);
        assertArrayEquals((byte[]) orig_12, result_12);
        assertArrayEquals((int[]) orig_13, result_13);
        assertArrayEquals((long[]) orig_14, result_14);
        assertArrayEquals((String[][]) orig_15, result_15);
        assertEquals(orig_16, result_16);
        assertEquals(orig_17, result_17);
    }

    public static<T> void read_encoded_test(TagBasedEncoder<T> encoder) {
        enter_values(encoder);
        T encoded_raw = encoder.getEncoded();
        do_tag_system_assertions_without_delete(encoder);

        encoder.clear();
        assertEquals(0, encoder.getTags().length);

        encoder.readFromEncoded(encoded_raw);

        do_tag_system_assertions_without_delete(encoder);
        do_tag_system_assertions_delete(encoder);
    }

    public static void do_stream_test(TagBasedEncoderBytes encoder) {
        byte[] orig = new SecureRandom().generateSeed(12);
        encoder.addEntry("orig1", orig);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(orig);
        encoder.addEntry("orig2", inputStream, orig.length);

        assertArrayEquals(orig, encoder.getEntry("orig1"));
        assertArrayEquals(orig, encoder.getEntry("orig2"));
        stream_getter_equality(encoder, "orig1", orig);
        stream_getter_equality(encoder, "orig2", orig);
    }
    public static void stream_getter_equality(TagBasedEncoderBytes encoder, String tag, byte[] orig) {
        try {
            InputStream is = encoder.getEntry_asLIStream(tag).r;
            byte[] buf = new byte[orig.length+16];//+16 to make sure that there is enough space to also fit the padded bits when using aes
            int read = is.read(buf);
            is.close();
            byte[] res = Arrays.copyOf(buf, read);
            assertArrayEquals(orig, res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getRandomNr(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
















    private static final String super_tag_1 = bool_test_tag;
    private static final String super_tag_2 = "596b9b673rb9a65b3a9rw36";
    private static final String super_tag_3 = "1";
    private static final String super_tag_4 = "";
    public static void enter_values(TupleTagBasedEncoder<?> encoder) {
        //some double adds that should not matter
        encoder.addEntryT(super_tag_1, bool_test_tag, bool_test_value);
        encoder.addEntryT(super_tag_1,"confuse???123123", 1234123+"WeirdTag ValueSwear");
        encoder.addEntryT(super_tag_2, str_test_tag, str_test_value);
        encoder.addEntryT(super_tag_1, "confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(super_tag_1, "confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(super_tag_1, int_test_tag,  int_test_value);
        encoder.addEntryT(super_tag_4, "WeirdTag confuse", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(super_tag_2, "confuse???", 1234123+"WeirdTagVa lueSwear");
        encoder.addEntryT(super_tag_2, weird_long_tag, weird_long_value);
        encoder.addEntryT(super_tag_4, empty_tag_test_tag, empty_tag_test_value);
        encoder.addEntryT(super_tag_2, empty_val_test_tag, empty_val_test_value);
        encoder.addEntryT(super_tag_2, "confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(super_tag_4, empty_strarr_test_tag, empty_strarr_test_value);  //empty is not written into the system.
        encoder.addEntryT(super_tag_2, strarr_test_tag, strarr_test_value);
        encoder.addEntryT(super_tag_2, empty_intarr_test_tag, empty_intarr_test_value);  //empty is not written into the system.
        encoder.addEntryT(super_tag_4, intarr_test_tag, intarr_test_value);
        encoder.addEntryT(super_tag_3, "confuse???1231235", 1234123+"WeirdTa gValueSwear");
        encoder.addEntryT(super_tag_2, strarr2d_test_tag, strarr2d_test_value);
        encoder.addEntryT(super_tag_4, "confuse???1231235", 1234123+"WeirdTa gValueSwear");
    }

    public static void do_tag_system_assertions_without_delete(TupleTagBasedEncoder<?> decoder) {
        assertTrue(decoder.exists(super_tag_2, str_test_tag));
        assertTrue(str_test_value.getBytes(StandardCharsets.UTF_8).length <= decoder.length(super_tag_2, str_test_tag)); //because the aes encrypting once might pad
        assertEquals(str_test_value, decoder.getEntryT(super_tag_2, str_test_tag, String.class));
        assertEquals(weird_long_value, decoder.getEntryT(super_tag_2, weird_long_tag, String.class));   //because the tag was deleted before
        assertEquals(bool_test_value, decoder.getEntryT(super_tag_1, bool_test_tag, boolean.class));
        assertEquals(empty_tag_test_value, decoder.getEntryT(super_tag_4, empty_tag_test_tag, String.class));
        assertArrayEquals(empty_strarr_test_value, decoder.getEntryT(super_tag_4, empty_strarr_test_tag, String[].class));
        assertArrayEquals(strarr_test_value, decoder.getEntryT(super_tag_2, strarr_test_tag, String[].class));
        assertArrayEquals(empty_intarr_test_value, decoder.getEntryT(super_tag_2, empty_intarr_test_tag, int[].class));
        assertArrayEquals(intarr_test_value, decoder.getEntryT(super_tag_4, intarr_test_tag, int[].class));
        assertTrue(Arrays.deepEquals(strarr2d_test_value, decoder.getEntryT(super_tag_2, strarr2d_test_tag, String[][].class)));
        assertEquals(int_test_value, decoder.getEntryT(super_tag_1, int_test_tag, int.class).intValue());
//        assertEquals(decoder.getTags()[0], bool_test_tag);  //may be wrong if tester added other stuff
    }
}
