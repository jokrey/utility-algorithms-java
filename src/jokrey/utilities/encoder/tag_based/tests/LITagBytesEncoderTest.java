package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.simple.data_structure.ExtendedIterator;
import jokrey.utilities.simple.data_structure.pairs.Pair;
import jokrey.utilities.debug_analysis_helper.TimeDiffMarker;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.encoder.examples.LITBE_DirectoryEncoder;
import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.bytes.file.FileStorage;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *
 * @author jokrey
 */
public class LITagBytesEncoderTest {
//    @Test
    public void do_larger_than_int_file_storage_test() throws IOException, StorageSystemException {
        File huge_test_file = new File(System.getProperty("user.home") + "/Desktop/huge_test_file.dontopen");
        huge_test_file.delete();

        TimeDiffMarker.println_setMark_d("writing huge file");
        byte[] just_some_stuff = new byte[]{1,23,12,123,54,35,7,5,7,85,68,63,46,5,67,45,78,65,76,34,123,126};
        byte[] just_some_stuff2 = new byte[]{3,2,1,1,2,3,4,5,6,7,8,9,10};
        byte[] arr4096 = new byte[4096*2*2*2];
        for(int i=0;i<arr4096.length;i++) {
            arr4096[i] = just_some_stuff[i%just_some_stuff.length];
        }
        long huge_test_file_size = (long) (Math.pow(2, 33)+1000);
        FileOutputStream fout = new FileOutputStream(huge_test_file);
        for(long i=0;i<huge_test_file_size;i+=arr4096.length) {
            fout.write(arr4096);
        }
        fout.close();
        TimeDiffMarker.println_setMark_d("wrote huge file");


        File file = new File(System.getProperty("user.home") + "/Desktop/litbe_test.litbe");
        FileStorage fcb_for_enc = new FileStorage(file, 32768);

        fcb_for_enc.setContent(new byte[]{});
        LITagBytesEncoder litbe_enc = new LITagBytesEncoder(fcb_for_enc);

        TimeDiffMarker.println_setMark_d("reading from huge file and writing entry");
        litbe_enc.addEntry("huge", new FileInputStream(huge_test_file), huge_test_file.length());
        TimeDiffMarker.println_setMark_d("wrote entry");

        TimeDiffMarker.println_setMark_d("writing small entry");
        litbe_enc.addEntry("small", just_some_stuff2);
        TimeDiffMarker.println_setMark_d("wrote small entry");

        TimeDiffMarker.println_setMark_d("reading entry and checking for equality");
        Pair<Long, InputStream> is = litbe_enc.getEntry_asLIStream("huge");
        byte[] buffer = new byte[arr4096.length];
        int read;
        boolean equal = true;
        while((read = is.r.read(buffer)) != -1) {
            for(int i=0;i<Math.min(read, buffer.length);i++) {
                if(buffer[i] != just_some_stuff[i%just_some_stuff.length])
                    equal=false;
            }
        }
        assertTrue(equal);
        TimeDiffMarker.println_setMark_d("read entry and check for equality in:");

        TimeDiffMarker.println_setMark_d("deleting huge entry"); //note how incredibly quick this is? :)
        litbe_enc.deleteEntry_noReturn("huge");
        TimeDiffMarker.println_setMark_d("deleted huge entry");

        assertArrayEquals(litbe_enc.getEntry("small"), just_some_stuff2);
    }


//    @Test
    public void run_directory_encoding_test() {
        try {
            LITBE_DirectoryEncoder.encode(
                    new File(System.getProperty("user.home") + "/Desktop/dir_enc_test"),
                    new File(System.getProperty("user.home") + "/Desktop/dir_enc_test.litbe")
            );

            LITBE_DirectoryEncoder.decode(
                    new File(System.getProperty("user.home") + "/Desktop/dir_enc_test.litbe"),
                    new File(System.getProperty("user.home") + "/Desktop/dir_enc_test_dec")
            );
            System.out.println("directory encoding went through. Correctness has to be checked manually tho for now");
        } catch(Exception ex) {
            System.out.println("directory encoding test failed. please note that a directory named dir_enc_test has to exist on the Desktop");
        }
    }


    @Test
    public void do_tag_system_iterator_test() throws IOException, StorageSystemException {
        byte[] orig = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        byte[] orig2 = new byte[]{91, 42, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};

        LITagBytesEncoder litbe_enc = new LITagBytesEncoder();

        litbe_enc.addEntry("1", orig);
        litbe_enc.addEntry("2", orig);
        litbe_enc.addEntry("3", orig2);

        //nestable prove of concept
        for(TagBasedEncoder.TaggedEntry<byte[]> e:litbe_enc) {
            System.out.println("tag outer: " + e.tag+", entry outer: "+ Arrays.toString(e.val));
            for(LITagBytesEncoder.TaggedStream entry:litbe_enc.getEntryIterator_stream()) {
                System.out.println("tag: " + entry.tag);

                byte[] buf = new byte[1000];
                int read = entry.stream.read(buf);
                byte[] res = Arrays.copyOf(buf, read);
                System.out.println("stream: " + Arrays.toString(res));
            }
        }
    }


    @Test
    public void do_tag_system_iterator_on_FILE_test() throws IOException, StorageSystemException {
        byte[] orig = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        byte[] orig2 = new byte[]{91, 42, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};

        File file = new File(System.getProperty("user.home") + "/Desktop/litbe_iterator_file_test.txt");
        FileStorage fcb_for_enc = new FileStorage(file);

        fcb_for_enc.setContent(new byte[]{});

        LITagBytesEncoder litbe_enc = new LITagBytesEncoder(fcb_for_enc);

        litbe_enc.addEntry("1", orig);
        litbe_enc.addEntry("2", orig);
        litbe_enc.addEntry("3", orig2);

        //nestable prove of concept
        for(TagBasedEncoder.TaggedEntry<byte[]> e:litbe_enc) {
            System.out.println("tag on file outer: " + e.tag+", entry on file outer: "+ Arrays.toString(e.val));
            for(LITagBytesEncoder.TaggedStream entry:litbe_enc.getEntryIterator_stream()) {
                System.out.println("tag on file: " + entry.tag);

                byte[] buf = new byte[1000];
                int read = entry.stream.read(buf);
                byte[] res = Arrays.copyOf(buf, read);
                System.out.println("stream on file: " + Arrays.toString(res));
            }
        }

        fcb_for_enc.close();
    }

    @Test
    public void do_tag_system_test() throws StorageSystemException {
        //order, values and type don't matter
        //just keep in mind that EACH tag has to have a different name!!!!


        LITagBytesEncoder encoder = new LITagBytesEncoder();
        TagSystemTestHelper.enter_values(encoder);

        byte[] encodedArray = encoder.getEncodedBytes();

        LITagBytesEncoder decoder = new LITagBytesEncoder(encodedArray);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(decoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(decoder);

        TagSystemTestHelper.basic_typed_system_test(encoder);
    }


    @Test
    public void do_li_file_test() throws StorageSystemException {
        TimeDiffMarker.setMark_d();
        try {
            File file = new File(System.getProperty("user.home") + "/Desktop/li_test.txt");
            FileStorage fcb_for_enc = new FileStorage(file);

            LIbae libae_enc = new LIbae(fcb_for_enc);

            fcb_for_enc.setContent(new byte[]{});

            byte[] shortone = new byte[]{1};
            byte[] orig = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
            byte[] orig2 = new byte[]{91, 42, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};
            byte[] origbig = new byte[]
                    {91, 111, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 91, 42, 3, 44, 5, 6, 78, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 1, 42, 3, 44, 5, 6, 76, 101, 5, 6, 12, 101, 5, 6, 123, 111, 5, 6, 117, 101,
                            23, 42, 3, 44, 5, 6, 65, 101, 5, 6, 117, 101, 5, 6, 98, 101, 5, 6, 117, 101, 23, 42, 3, 44, 5, 6, 98, 101, 5, 6, 117, 101, 5, 6, 90, 123, 5, 6, 43, 101, 5, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 6, 5, 6, 117, 101, 5, 6, 117, 101,
                            43, 42, 2, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 76, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 56, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101,
                            23, 42, 3, 44, 5, 6, 65, 101, 5, 6, 117, 101, 5, 6, 98, 101, 5, 6, 117, 101, 23, 42, 3, 44, 5, 6, 98, 101, 5, 6, 117, 101, 5, 6, 90, 123, 5, 6, 43, 101, 5, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 6, 5, 6, 117, 101, 5, 6, 117, 101,
                            43, 42, 2, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 76, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 56, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101,
                            23, 42, 3, 44, 5, 6, 65, 101, 5, 6, 117, 101, 5, 6, 98, 101, 5, 6, 117, 101, 23, 42, 3, 44, 5, 6, 98, 101, 5, 6, 117, 101, 5, 6, 90, 123, 5, 6, 43, 101, 5, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 6, 5, 6, 117, 101, 5, 6, 117, 101,
                            43, 42, 2, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 76, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 56, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};
            libae_enc.encode(origbig);
            libae_enc.encode(orig);
            libae_enc.encode(orig2, orig, origbig, orig2, orig);
            libae_enc.encode(shortone);


            fcb_for_enc.close();


            FileStorage fcb_for_dec = new FileStorage(file);
            ExtendedIterator<byte[]> libae_iter = new LIbae(fcb_for_dec).iterator();

            libae_iter.remove();//leading origbig read into nothingness and deleted
            byte[] decoded = libae_iter.next_or_null();
            byte[][] decoded2 = libae_iter.next(4);
            byte[] decoded3 = libae_iter.next_or_null();
            byte[] decoded4 = libae_iter.next_or_null();
            byte[] shouldbenull_lastdec = libae_iter.next_or_null();

            assertArrayEquals(decoded, orig);
            assertArrayEquals(decoded2[0], orig2);
            assertArrayEquals(decoded2[1], orig);
            assertArrayEquals(decoded2[2], origbig);
            assertArrayEquals(decoded2[3], orig2);
            assertArrayEquals(decoded4, shortone);
            assertArrayEquals(decoded2[1], orig);
            assertNull(shouldbenull_lastdec);

            fcb_for_dec.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        TimeDiffMarker.println_d("li file test took: ");
    }


    @Test
    public void do_li_test() throws StorageSystemException {
        TimeDiffMarker.setMark_d();
        LIbae libae_enc = new LIbae();

        byte[] orig = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        byte[] orig2 = new byte[]{91, 42, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};
        byte[] origbig = new byte[]
                {91, 111, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 91, 42, 3, 44, 5, 6, 78, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 1, 42, 3, 44, 5, 6, 76, 101, 5, 6, 12, 101, 5, 6, 123, 111, 5, 6, 117, 101,
                        23, 42, 3, 44, 5, 6, 65, 101, 5, 6, 117, 101, 5, 6, 98, 101, 5, 6, 117, 101, 23, 42, 3, 44, 5, 6, 98, 101, 5, 6, 117, 101, 5, 6, 90, 123, 5, 6, 43, 101, 5, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 6, 5, 6, 117, 101, 5, 6, 117, 101,
                        43, 42, 2, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 76, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 56, 42, 3, 44, 5, 6, 45, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};
        libae_enc.encode(orig);
        libae_enc.encode(orig2, orig, origbig, orig2, orig);
        byte[] encoded = libae_enc.getEncodedBytes();
        System.out.println(Arrays.toString(encoded));

        ExtendedIterator<byte[]> libae_dec = new LIbae(encoded).iterator();
        byte[] decoded = libae_dec.next();
        byte[][] decoded2 = libae_dec.next(4);
        byte[] decoded3 = libae_dec.next();
        byte[] shouldbenull_lastdec = libae_dec.next_or_null();

//        System.out.println("or: " + Arrays.toString(orig));
//        System.out.println("en: " + Arrays.toString(encoded));
//        System.out.println("de: " + Arrays.toString(decoded));
        assertArrayEquals(orig, decoded);
        assertArrayEquals(orig2, decoded2[0]);
        assertArrayEquals(orig, decoded2[1]);
        assertArrayEquals(origbig, decoded2[2]);
        assertArrayEquals(orig2, decoded2[3]);
        assertArrayEquals(orig, decoded3);
        assertNull(shouldbenull_lastdec);
        TimeDiffMarker.println_d("li test took: ");
    }


    @Test
    public void do_litbe_test() {
        TimeDiffMarker.setMark_d();
        byte[] orig = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        byte[] orig2 = new byte[]{91, 42, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};

        LITagBytesEncoder litbe_enc = new LITagBytesEncoder();

        try {
            litbe_enc.addEntry("purposeful error", new ByteArrayInputStream(orig), orig.length + 20);
            fail("should have thrown an exception");
        } catch(StorageSystemException re) {
            //supposed to happen
        }

        litbe_enc.addEntry("readme", new ByteArrayInputStream(orig2), orig2.length);
        litbe_enc.addEntry("test", orig);
        litbe_enc.addEntry("test2", orig2);
        litbe_enc.addEntry("confuse123", orig2);
        litbe_enc.addEntry("test3", orig2);
        litbe_enc.addEntry("empty", new byte[0]);
        litbe_enc.addEntry("confuse1", orig2);




        byte[] encoded = litbe_enc.getEncodedBytes();
        LITagBytesEncoder litbe_dec = new LITagBytesEncoder(encoded);

        assertArrayEquals(litbe_dec.deleteEntry("test"), orig);
        assertArrayEquals(litbe_dec.getEntry("test2"), orig2);
        assertArrayEquals(litbe_dec.getEntry("empty"), new byte[0]);
        assertTrue(litbe_dec.exists("test2"));
        assertNull(litbe_dec.getEntry("test", null));
        assertEquals(0, litbe_dec.getEntry("empty").length);

        assertArrayEquals(litbe_dec.getTags(),
                new String[] {"purposeful error", "readme", "test2", "confuse123", "test3", "empty", "confuse1"});
        assertArrayEquals(litbe_dec.getEntry("readme"), orig2);

        System.out.println("padded?: " + Arrays.toString(litbe_dec.getEntry("purposeful error")));

        TimeDiffMarker.println_d("litbe test took: ");

        TagSystemTestHelper.do_stream_test(litbe_dec);
    }


    @Test
    public void do_litbe_file_test() throws StorageSystemException {
        TimeDiffMarker.setMark_d();
        try {
            File file = new File(System.getProperty("user.home") + "/Desktop/litbe_test.txt");
            FileStorage fcb_for_enc = new FileStorage(file);

            fcb_for_enc.setContent(new byte[]{});

            byte[] orig = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
            byte[] orig2 = new byte[]{91, 42, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};

            LITagBytesEncoder litbe_enc = new LITagBytesEncoder(fcb_for_enc);

            try {
                litbe_enc.addEntry("purposeful error", new ByteArrayInputStream(orig), orig.length + 20);
                fail("should have thrown an exception");
            } catch(StorageSystemException re) {
                //supposed to happen
            }

            litbe_enc.addEntry("readme", new ByteArrayInputStream(orig2), orig2.length);
            litbe_enc.addEntry("test", orig);
            litbe_enc.addEntry("test2", orig2);
            litbe_enc.addEntry("confuse1231", orig2);
            litbe_enc.addEntry("", orig2);//empty tag test
            litbe_enc.addEntry("test3", orig2);
            litbe_enc.addEntry("test4", orig2);
            litbe_enc.addEntry("confuse1", orig2);

            fcb_for_enc.close();



            FileStorage fcb_for_dec = new FileStorage(file);
            LITagBytesEncoder litbe_dec = new LITagBytesEncoder(fcb_for_dec);

            TagSystemTestHelper.do_stream_test(litbe_dec);

            assertArrayEquals(litbe_dec.deleteEntry("test"), orig);
            assertArrayEquals(litbe_dec.deleteEntry("test2"), orig2);
            assertArrayEquals(litbe_dec.deleteEntry("test3"), orig2);
            assertArrayEquals(litbe_dec.getEntry(""), orig2);//empty tag test
//            System.out.println(Arrays.toString(litbe_dec.getEntryT("test2", null)));
            assertNull(litbe_dec.getEntry("test2", null));
            assertArrayEquals(litbe_dec.getEntry("readme"), orig2);
            System.out.println("padded?: " + Arrays.toString(litbe_dec.getEntry("purposeful error")));

            try {
                Pair<Long, InputStream> is = litbe_dec.getEntry_asLIStream("test4");
                byte[] buf = new byte[1000];
                int read = is.r.read(buf);
                byte[] res = Arrays.copyOf(buf, read);
                assertArrayEquals(res, orig2);
            } catch (IOException e) {
                e.printStackTrace();
                fail("reading test4 threw unexpected io exception");
            }

            fcb_for_dec.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TimeDiffMarker.println_d("litbe file test took: ");
    }


//    @Test
    public void do_litbe_file_performance_and_size_test() throws StorageSystemException {
        TimeDiffMarker.setMark_d();
        try {
            File file = new File(System.getProperty("user.home") + "/Desktop/litbe_perf_size_test.txt");
            FileStorage fcb_for_enc = new FileStorage(file);

            fcb_for_enc.setContent(new byte[]{});

            byte[] orig = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
            byte[] orig2 = new byte[]{91, 42, 3, 44, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101, 5, 6, 117, 101};
            byte[] hugearray = new byte[600000000];
            for(int i=0;i<hugearray.length;i++) {
                hugearray[i] = orig2[i % orig2.length];
            }
            TimeDiffMarker.setMark(1);
            TimeDiffMarker.setMark(2);

            LITagBytesEncoder litbe_enc = new LITagBytesEncoder(fcb_for_enc);
            litbe_enc.addEntry("test2", orig2);
            litbe_enc.addEntry("test", orig);
            litbe_enc.addEntry("", orig2);//empty tag test
            litbe_enc.addEntry("huge", hugearray);
            litbe_enc.addEntry("huge1", hugearray);
            litbe_enc.addEntry("huge2", hugearray);
            litbe_enc.addEntry("huge3", hugearray);
            litbe_enc.addEntry("huge4", hugearray);
            TimeDiffMarker.println_setMark(2, "encoding took: ");

            fcb_for_enc.close();
            TimeDiffMarker.println_setMark(2, "closing after encode took: ");


            TimeDiffMarker.setMark(2);

            FileStorage fcb_for_dec1 = new FileStorage(file);
            LITagBytesEncoder litbe_dec = new LITagBytesEncoder(fcb_for_dec1);

            assertArrayEquals(litbe_dec.getEntry("huge"), hugearray);
            assertArrayEquals(litbe_dec.getEntry("huge1"), hugearray);
            assertArrayEquals(litbe_dec.getEntry("huge2"), hugearray);
            assertArrayEquals(litbe_dec.getEntry("huge3"), hugearray);
            assertArrayEquals(litbe_dec.getEntry("huge4"), hugearray);
            assertArrayEquals(litbe_dec.getEntry("test"), orig);
            assertArrayEquals(litbe_dec.getEntry("test2"), orig2);
            assertArrayEquals(litbe_dec.getEntry(""), orig2);  //empty tag test
            TimeDiffMarker.println_setMark(2, "decoding WITHOUT delete took: ");
            fcb_for_dec1.close();
            TimeDiffMarker.println_setMark(2, "closing WITHOUT delete took: ");

            FileStorage fcb_for_dec2 = new FileStorage(file);
            LITagBytesEncoder litbe_dec2 = new LITagBytesEncoder(fcb_for_dec2);

            assertArrayEquals(litbe_dec2.deleteEntry("huge"), hugearray);
            assertArrayEquals(litbe_dec2.deleteEntry("huge1"), hugearray);
            assertArrayEquals(litbe_dec2.deleteEntry("huge2"), hugearray);
            assertArrayEquals(litbe_dec2.deleteEntry("huge3"), hugearray);
            assertArrayEquals(litbe_dec2.deleteEntry("huge4"), hugearray);
            assertArrayEquals(litbe_dec2.deleteEntry("test2"), orig2);
            assertArrayEquals(litbe_dec2.getEntry("test"), orig);
            assertArrayEquals(litbe_dec2.deleteEntry(""), orig2);  //empty tag test
            TimeDiffMarker.println(2, "decoding WITH delete took: ");

            System.out.println("getentrytest:: "+Arrays.toString(litbe_dec2.getEntry("test")));
            assertNull(litbe_dec2.getEntry("test2", null));

            fcb_for_dec2.close();
            TimeDiffMarker.println(2, "closing WITH delete took: ");

        } catch (IOException e) {
            e.printStackTrace();
        }
        TimeDiffMarker.println_d("litbe file perf and size test took: ");
        TimeDiffMarker.println(1, "litbe file perf and size test took(without hugearray allocation): ");
    }




//    @Test
    public void directory_encoder_test() throws Exception {
        String test_dir_orig_path = "F:\\";
        String target_file_path = "C:\\Users\\User\\Desktop\\test_java.litbe";
        String test_dir_out_path = "C:\\Users\\User\\Desktop\\dec_j";

        TimeDiffMarker.setMark(0);
        TimeDiffMarker.setMark(1);
        long error_count = LITBE_DirectoryEncoder.encode(new File(test_dir_orig_path), new File(target_file_path));
        TimeDiffMarker.println_setMark(1, "encoding(with "+error_count+" errors) took");
        error_count = LITBE_DirectoryEncoder.decode(new File(target_file_path), new File(test_dir_out_path));
        TimeDiffMarker.println(1, "decoding(with "+error_count+" errors) took");
        TimeDiffMarker.println(0, "Complete dir encoder test took");
    }
}