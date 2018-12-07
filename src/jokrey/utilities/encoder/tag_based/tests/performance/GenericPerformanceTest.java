package jokrey.utilities.encoder.tag_based.tests.performance;

import jokrey.utilities.debug_analysis_helper.AverageCallTimeMarker;
import jokrey.utilities.debug_analysis_helper.TimeDiffMarker;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;

/**
 * Used to COMPARE performance of different Tbe's.
 * Does NOT compare performance of type conversion(those are often different).
 *     Test the TypeGenerators directly for that.
 * This is NOT a functionality test. Use the TagSystemTestHelper for that.
 */
public class GenericPerformanceTest {
    public static byte[] generate_utf8_conform_byte_array(int size) {
        byte[] arr = new byte[size];
        byte[] string = "hey. This is a test.".getBytes(StandardCharsets.UTF_8); //any character in the string has to be ascii - because ascii fits into a single byte.
        for(int i=0;i<arr.length;i++) {
            arr[i] = string[i%string.length];
        }
        return arr;
    }

    public interface WriteResults {
        void write(String introduction_text, AverageCallTimeMarker.Call_Count_Average[] results);
    }

    //standard combined tests - combines high count
    private static final byte[] small_value = GenericPerformanceTest.generate_utf8_conform_byte_array(10);
    private static final byte[] mid_value = GenericPerformanceTest.generate_utf8_conform_byte_array(10*10000);
    private static final byte[] big_value = GenericPerformanceTest.generate_utf8_conform_byte_array(10*1000000);
    protected static void run_standard_test(String description, WriteResults writeResults, TagBasedEncoder<byte[]> tbe) {
        if(description!=null)TimeDiffMarker.println_setMark_d(description);

        AverageCallTimeMarker.Call_Count_Average[] many_small_res = GenericPerformanceTest.test_print_all(tbe, 1000, 10, small_value);
        AverageCallTimeMarker.Call_Count_Average[] few_small_res = GenericPerformanceTest.test_print_all(tbe, 100, 30, small_value);
        AverageCallTimeMarker.Call_Count_Average[] few_big_res = GenericPerformanceTest.test_print_all(tbe, 100, 30, mid_value);

        AverageCallTimeMarker.Call_Count_Average[] combined_res = AverageCallTimeMarker.combine_all(many_small_res, few_small_res, few_big_res);
        AverageCallTimeMarker.print_all(combined_res, "Intermediate results(long run) for: "+tbe.getClass());

        if(description!=null) writeResults.write("FULL RUN\n"+description + "     total time taken ("+TimeDiffMarker.getDiffFor_as_string_d()+")", combined_res);
        if(description!=null) TimeDiffMarker.println_setMark_d(description);
    }
    protected static void run_standard_test_string(String description, WriteResults writeResults, TagBasedEncoder<String> tbe) {
        TimeDiffMarker.println_setMark_d(description);

        AverageCallTimeMarker.Call_Count_Average[] many_small_res = GenericPerformanceTest.test_print_all(tbe, 1000, 10, new String(small_value, StandardCharsets.UTF_8));
        AverageCallTimeMarker.Call_Count_Average[] few_small_res = GenericPerformanceTest.test_print_all(tbe, 100, 30, new String(small_value, StandardCharsets.UTF_8));
        AverageCallTimeMarker.Call_Count_Average[] few_big_res = GenericPerformanceTest.test_print_all(tbe, 100, 30, new String(mid_value, StandardCharsets.UTF_8));

        AverageCallTimeMarker.Call_Count_Average[] combined_res = AverageCallTimeMarker.combine_all(many_small_res, few_small_res, few_big_res);
        AverageCallTimeMarker.print_all(combined_res, "Intermediate results(string run) for: "+tbe.getClass());

        writeResults.write("FULL RUN\n"+description + "     total time taken ("+TimeDiffMarker.getDiffFor_as_string_d()+")", combined_res);
        TimeDiffMarker.println_setMark_d(description);
    }
    protected static void run_standard_test_short(String description, WriteResults writeResults, TagBasedEncoder<byte[]> tbe) {
        TimeDiffMarker.println_setMark_d(description);

        AverageCallTimeMarker.Call_Count_Average[] many_small_res = GenericPerformanceTest.test_print_all(tbe, 250, 1, small_value);
        AverageCallTimeMarker.Call_Count_Average[] few_small_res = GenericPerformanceTest.test_print_all(tbe, 10, 3, small_value);
        AverageCallTimeMarker.Call_Count_Average[] few_big_res = GenericPerformanceTest.test_print_all(tbe, 10, 3, mid_value);

        AverageCallTimeMarker.Call_Count_Average[] combined_res = AverageCallTimeMarker.combine_all(many_small_res, few_small_res, few_big_res);
        AverageCallTimeMarker.print_all(combined_res, "Intermediate results(short run) for: "+tbe.getClass());

        writeResults.write("SHORTENED RUN\n"+description + "     total time taken ("+TimeDiffMarker.getDiffFor_as_string_d()+")", combined_res);
        TimeDiffMarker.println_setMark_d(description);
    }
    protected static void run_standard_test_big(String description, WriteResults writeResults, TagBasedEncoder<byte[]> tbe) {
        TimeDiffMarker.println_setMark_d(description);

        AverageCallTimeMarker.Call_Count_Average[] mid_res = GenericPerformanceTest.test_print_all(tbe, 100, 1, mid_value);
        AverageCallTimeMarker.Call_Count_Average[] big_res = GenericPerformanceTest.test_print_all(tbe, 10, 1, big_value);

        AverageCallTimeMarker.Call_Count_Average[] combined_res = AverageCallTimeMarker.combine_all(mid_res, big_res);
        AverageCallTimeMarker.print_all(combined_res, "Intermediate results(short run) for: "+tbe.getClass());

        writeResults.write("BIG RUN\n"+description + "     total time taken ("+TimeDiffMarker.getDiffFor_as_string_d()+")", combined_res);
        TimeDiffMarker.println_setMark_d(description);
    }
    protected static void run_standard_test_many(String description, WriteResults writeResults, TagBasedEncoder<byte[]> tbe) {
        TimeDiffMarker.println_setMark_d(description);

        AverageCallTimeMarker.Call_Count_Average[] many_res = GenericPerformanceTest.test_print_all(tbe, 10000, 1, small_value);

        AverageCallTimeMarker.Call_Count_Average[] combined_res = AverageCallTimeMarker.combine_all(many_res);
        AverageCallTimeMarker.print_all(combined_res, "Intermediate results(short run) for: "+tbe.getClass());

        writeResults.write("MANY RUN\n"+description + "     total time taken ("+TimeDiffMarker.getDiffFor_as_string_d()+")", combined_res);
        TimeDiffMarker.println_setMark_d(description);
    }
    
    

    /**
     * Will println a lot of stuff.
     * Only the final results are REALLY relevant.
     * Especially how long a test part took may be weird. This is due to the fact that apart from the actual test, we require some overhead (that overhead may be significantly longer that the test itself).
     */
    protected static AverageCallTimeMarker.Call_Count_Average[] test_print_all_default(TagBasedEncoder<byte[]> tbe) {
        byte[] value = generate_utf8_conform_byte_array(1000);
        return test_print_all(tbe, 1000, 101, value);
    }

    /**
     * tbe will be cleared
     *
     * Will println a lot of stuff.
     * Only the final results are REALLY relevant.
     * Especially how long a test part took may be weird. This is due to the fact that apart from the actual test, we require some overhead (that overhead may be significantly longer that the test itself).
     */
    protected static<SF> AverageCallTimeMarker.Call_Count_Average[] test_print_all(TagBasedEncoder<SF> tbe, int count, int iterations, SF value) {
        String value_print = value.getClass().isArray() ?
                                            value.getClass().getName()+"["+Array.getLength(value)+"]" :
                                            value instanceof String ?
                                                    "Str(l="+((String)value).length()+")" : value.toString();
        String description_print = tbe.getClass()+" - tag-count: "+count+" - repeat-iterations: "+iterations+" - value: \""+value_print+"\"";

        tbe.clear();
        AverageCallTimeMarker.Call_Count_Average[] add_res = add_test_perf(tbe, count, iterations, value, false);
        AverageCallTimeMarker.Call_Count_Average[] search_res = search_test_perf(tbe, count, iterations, value, false);
        AverageCallTimeMarker.Call_Count_Average[] delete_res = delete_test_perf(tbe, count, iterations, value, false);
        tbe.clear();

        AverageCallTimeMarker.Call_Count_Average[] combined_results = AverageCallTimeMarker.combine_all(add_res, search_res, delete_res);

        AverageCallTimeMarker.print_all(combined_results, description_print);

        return combined_results;
    }


    private static<SF> void add_values_nocheck(TagBasedEncoder<SF> tbe, int count, SF value) {
        for(int i=0;i<count;i++)
            tbe.addEntry_nocheck(String.valueOf(i), value);
    }
    
    /**
     * Will clear the tbe initially and when finished
     * 
     * Runs many of the following operations:
     * addEntry_nocheck(tag_dont_worry_about_it_is_unique, value)
     * addEntry_nocheck(tag_dont_worry_about_it_is_unique, mid_value)
     *
     * gives a println out in the middle
     *
     * @param tbe any TagBasedEncoder
     * @param <SF> not to worry
     * @return results
     */
    protected static<SF> AverageCallTimeMarker.Call_Count_Average[] add_test_perf(TagBasedEncoder<SF> tbe, int count, int iterations, SF value, boolean from_scratch) {
        TbePerformanceMonitor<SF> monitor = new TbePerformanceMonitor<>(tbe);
        
        if(from_scratch)
            tbe.clear();
        AverageCallTimeMarker.clear();

        for(int c=0;c<iterations;c++) {
            add_values_nocheck(monitor, count, value);
            if(c+1< iterations)
                tbe.clear();
        }
        
        if(from_scratch)
            tbe.clear();

        return AverageCallTimeMarker.get_all();
    }
    
    
    /**
     * Will clear the tbe initially and when finished
     * 
     * Runs many of the following operations:
     * exists_in_cache, getTags, getEntry
     *     Those are the operations that utilise the specific search algorith, without altering data.
     *
     * @param tbe any TagBasedEncoder
     * @param <SF> not to worry
     * @return results
     */
    protected static<SF> AverageCallTimeMarker.Call_Count_Average[] search_test_perf(TagBasedEncoder<SF> tbe, int count, int iterations, SF value, boolean from_scratch) {
        TbePerformanceMonitor<SF> monitor = new TbePerformanceMonitor<>(tbe);

        if(from_scratch) {
            tbe.clear();
            add_values_nocheck(tbe, count, value);
        }
        AverageCallTimeMarker.clear();

        for(int c=0;c<iterations;c++)
            for(int i=0;i<count;i++)
                monitor.exists(String.valueOf(i));

        for(int c=0;c<iterations;c++)
            for(int i=0;i<count;i++)
                monitor.getEntry(String.valueOf(i));

        for(int c=0;c<iterations;c++)
            monitor.getTags();
        
        if(from_scratch)
            tbe.clear();

        return AverageCallTimeMarker.get_all();
    }


    /**
     * Will clear the tbe initially and when finished
     *
     * Runs many of the following operations:
     * deleteEntry, deleteEntry, clear
     *
     * @param tbe any TagBasedEncoder
     * @param <SF> not to worry
     * @return results
     */
    protected static<SF> AverageCallTimeMarker.Call_Count_Average[] delete_test_perf(TagBasedEncoder<SF> tbe, int count, int iterations, SF value, boolean from_scratch) {
        TbePerformanceMonitor<SF> monitor = new TbePerformanceMonitor<>(tbe);

        if(from_scratch) {
            tbe.clear();
            add_values_nocheck(tbe, count, value);
        }
        AverageCallTimeMarker.clear();

        for(int c=0;c<iterations;c++) {
            for (int i = 0; i < count; i++)
                monitor.deleteEntry_noReturn(String.valueOf(i));
            add_values_nocheck(tbe, count, value);
        }

        for(int c=0;c<iterations;c++) {
            for (int i = count-1; i>=0; i--)
                monitor.deleteEntry_noReturn(String.valueOf(i));
            add_values_nocheck(tbe, count, value);
        }

        for(int c=0;c<iterations;c++) {
            for (int i = 0; i < count; i++)
                monitor.deleteEntry(String.valueOf(i));
            add_values_nocheck(tbe, count, value);
        }

        for(int c=0;c<iterations;c++) {
            for (int i = count-1; i>=0; i--)
                monitor.deleteEntry(String.valueOf(i));
            add_values_nocheck(tbe, count, value);
        }

        for(int c=0;c<iterations;c++) {
            monitor.clear();
            if(c+1<iterations)
                add_values_nocheck(tbe, count, value);
        }


        if(from_scratch)
            tbe.clear();
        
        return AverageCallTimeMarker.get_all();
    }
}
