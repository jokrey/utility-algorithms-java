package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.compression.deflate.DeflateAlgorithmHelper;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.compression.deflate.DeflateContentCompressingBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for the EncryptingBytesEncoder
 *
 * @author jokrey
 */
public class DeflateCompressingBytesEncoderTest {
    @Test
    public void simple_comparing_tag_system_test() {
        LITagBytesEncoder raw_encoder = new LITagBytesEncoder();
        DeflateContentCompressingBytesEncoder content_compressing_encoder = new DeflateContentCompressingBytesEncoder(new LITagBytesEncoder());

        TagSystemTestHelper.enter_values(raw_encoder);
        TagSystemTestHelper.enter_values(content_compressing_encoder);

        //what follows is the worst possible way to check whether or not it has been properly encrypted:
        assertFalse(Arrays.equals(raw_encoder.getEncodedBytes(), content_compressing_encoder.getEncodedBytes()));
//        assertTrue(raw_encoder.getEncodedBytes().length > content_compressing_encoder.getEncodedBytes().length);  //CANNOT BE GENERALLY ASSERTED--- Compression will only work with some data.

        TagSystemTestHelper.do_tag_system_assertions_without_delete(raw_encoder);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(content_compressing_encoder);

        TagSystemTestHelper.basic_typed_system_test(raw_encoder);
        TagSystemTestHelper.basic_typed_system_test(content_compressing_encoder);

        TagSystemTestHelper.do_tag_system_assertions_delete(raw_encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(content_compressing_encoder);

        TagSystemTestHelper.do_stream_test(raw_encoder);
        TagSystemTestHelper.do_stream_test(content_compressing_encoder);
    }

    //NOTE:: especially with random data compression is not generally possible.
    @Test
    public void simple_compression_test() {
        run_compression_test(new SecureRandom().generateSeed(100000), 9);
        byte[] test = new byte[10000];
        for(int i=0;i<test.length;i++)
            test[i] = (byte) (i%40);
        run_compression_test(test, 3);
        run_compression_test(test, 9);
    }
    private void run_compression_test(byte[] orig, int level) {
        byte[] compressed = DeflateAlgorithmHelper.compress(orig, level);
        byte[] decompressed = DeflateAlgorithmHelper.decompress(compressed);

        assertArrayEquals(orig, decompressed);

        System.out.println("compressed length ratio: "+((double) compressed.length) / orig.length);
    }
}