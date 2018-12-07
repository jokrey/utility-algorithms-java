package jokrey.utilities.encoder.tag_based.implementation.length_indicator.string;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.TransparentStorage;
import jokrey.utilities.transparent_storage.string.non_persistent.StringStorageSystem;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * PURE - NOT WORKING VERSION - Display concept
 *
 * OLD LISE FUNCTIONALITY - cooler,
 *    uses nested li indicators. i.e. the first char indicates how many chars the actual indicator has, unless it has more than 9, then the next one does and so on..
 *      sadly it  does not work when trying to encode certain integers - does not work in ALL situations (demonstrated below)
 *      even more sad: once one fixed it by adding a "not-number-character" to the end of li and ignoring that character on decode (which fixed the issues) -
 *                   at that point we don't need the li at all anymore...
 *
 * @author jokrey
 * @deprecated
 */
public class LIseLegacy_Pure extends LIse {
    public LIseLegacy_Pure() {}
    public LIseLegacy_Pure(String encoded) {super(encoded);}







    ///PURE VERSION (does not always work)
    @Override protected String getLengthIndicatorFor(String str) {
        ArrayList<String> lengthIndicators = new ArrayList<>();
        lengthIndicators.add(String.valueOf(str.length()));
        while (lengthIndicators.get(0).length() != 1)
            lengthIndicators.add(0, String.valueOf(lengthIndicators.get(0).length()));
        return LIse.toString(lengthIndicators, "");
    }
    @Override protected long[] get_next_li_bounds(Position start_pos, TransparentStorage<String> current) {
        long i=get(start_pos);
        if(i+1>current.contentSize())return null;

        String indicatedSubstring = current.sub(i, i+=1); //we parse the initial first char. It is ALWAYS a length indicator.
        int indicatedSubstring_parsed = parseInt(indicatedSubstring, -1);
        int lastIndicatedSubstring_length; //last we parsed nothing, so length was 0

        if(indicatedSubstring_parsed==-1) //First char has to be a length indicator.
            throw new StorageSystemException("The string appears to have been illegally altered");

        int lastIndicatedSubstring_parse; //last we parsed nothing, so 0
        do {
            lastIndicatedSubstring_length = indicatedSubstring.length();
            lastIndicatedSubstring_parse = indicatedSubstring_parsed;

            indicatedSubstring = current.sub(i, (i+indicatedSubstring_parsed));
            i+=lastIndicatedSubstring_parse;
            indicatedSubstring_parsed = parseInt(indicatedSubstring, -1);

        } while(indicatedSubstring_parsed!=-1 && // when the current substring cannot be parsed to a number, then it has to be the desired content
                i + indicatedSubstring_parsed <= current.contentSize() && // if the next substring call would throw an exception,
                //     then we know our current number is too high to still be an indicator and is therefore the content
                indicatedSubstring.length() > lastIndicatedSubstring_length); //any li has to gain in length, which implies a gain in parsed number by a factor of min 10.

        return new long[]{i-lastIndicatedSubstring_parse, i};
    }



    public static class PureTest {
        @Test
        public void test_old_li_code_proof_of_general_concept() {
            test_one_run("", "0", "1", "2", "Hello.", "900", "Some people already had enough of this. But they were not important. Sadly.", "10", "21", "100");
            test_one_run("", "0", "1", "2", "Hello.", "200", "Some people already had enough of this. But they were not important. Sadly.");
        }
        void test_one_run(String... test_strs) {
            LIseLegacy_Pure pure = new LIseLegacy_Pure();
            pure.li_encode(test_strs);

            Position read_pointer = pure.reset();
            for (String test_str1 : test_strs) {
                String decoded = pure.li_decode(read_pointer);
                assertEquals(test_str1, decoded);
            }
        }

        @Test
        public void test_old_li_code_proof_of_flaw() {
            String number_content = "10"; //will become: "210"
            String padd_of_literally_anything_greater_or_equal_10 = "1234567890";
            String[] add_arr = {number_content, padd_of_literally_anything_greater_or_equal_10};

            StringStorageSystem encoded = new StringStorageSystem();
            for (String orig : add_arr)
                encoded.append(new LIseLegacy_Pure().getLengthIndicatorFor(orig)).append(orig);

//      System.out.println(encoded); //will be(correctly so): 2109123456789

            long[] s_e_de = new LIseLegacy_Pure().get_next_li_bounds(new LIseLegacy_Pure().reset(), encoded);
            String decoded = encoded.sub(s_e_de[0], s_e_de[1]);

//      System.out.println(decoded); //should be: "10", will be: 2101234567
//      assertEquals(number_content, decoded);  //doesn't, instead::
            assertNotEquals(number_content, decoded);


            //proof it works with hash/"not-number-character":
            encoded = new StringStorageSystem();
            for (String orig : add_arr)
                encoded.append(new LIseLegacy_Fixed().getLengthIndicatorFor(orig)).append(orig);

            s_e_de = new LIseLegacy_Fixed().get_next_li_bounds(new LIseLegacy_Fixed().reset(), encoded);
            decoded = encoded.sub(s_e_de[0], s_e_de[1]);

            assertEquals(number_content, decoded);
        }

        @Test
        public void listmanager_still_works() {
            String identifer = "2(c234rf%&%11%&%11%&%1111%&%11%&%11%&%112bl233(f%&%26%&%11%&%2016%&%7%&%19%&%112)d3111b6pTitle219hFutter - Essen qwe211iImportance2q35oNote255qBratkartoffeln%\\n\\%Kartoffelpuffer%\\n\\%Fisch%\\n\\%Pilze%\\n\\%Spiegeleier";

            LITagStringEncoder outerdecoder = new LITagStringEncoder(new LIseLegacy_Fixed(identifer));
//            DateTime c = new DateTime();
//            c.readFromSaveString(outerdecoder.deleteEntry("c"));
//            System.out.println(c);
//            DateTime l = new DateTime();
//            l.readFromSaveString(outerdecoder.deleteEntry("l"));
//            System.out.println(l);
            LITagStringEncoder innerdecoder = new LITagStringEncoder(new LIseLegacy_Fixed(outerdecoder.getEntry("d")));
            assertEquals("6pTitle219hFutter - Essen qwe211iImportance2q35oNote255qBratkartoffeln%\\n\\%Kartoffelpuffer%\\n\\%Fisch%\\n\\%Pilze",
                    innerdecoder.getEncodedString());
            assertEquals("Futter - Essen qwe", innerdecoder.deleteEntry("Title"));
            assertEquals("3", innerdecoder.deleteEntry("Importance"));
            assertEquals("Bratkartoffeln%\\n\\%Kartoffelpuffer%\\n\\%Fisch%\\n\\%Pilze", innerdecoder.deleteEntry("Note"));
        }
    }


    /**
     * guaranteed to return a positive number
     *
     * COPY FROM: {@link Integer#parseInt(String, int)}
     *            But uses return, instead of throw as flow control - which is better, because less weird.
     *
     * @param s string assumed to be an integer of radix 10
     * @param fallback integer to be returned if provided string s is not an integer
     * @return s as int or fallback
     */
    protected static int parseInt( final String s, int fallback ) {
        if (s == null || s.isEmpty())
            return fallback;

        int sign = -1;
        final int len = s.length();
        final char ch = s.charAt(0);
        int num  = -(ch - '0');
        if (num > 0 || num < -9)  //malformed
            return fallback;

        final int max = -Integer.MAX_VALUE;
        final int multmax = max / 10;
        int i = 1;
        while (i < len) {
            int d = s.charAt(i++) - '0';
            if (d < 0 || d > 9)  //malformed
                return fallback;
            if (num < multmax)  //overflow
                return fallback;
            num *= 10;
            if (num < (max+d))  //overflow
                return fallback;
            num -= d;
        }

        return sign * num;
    }

}
