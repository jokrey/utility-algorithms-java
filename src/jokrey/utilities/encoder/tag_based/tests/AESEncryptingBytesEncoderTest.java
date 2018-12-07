package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.encryption.aes.AESContentEncryptingBytesEncoder;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.encryption.aes.AESEncryptingBytesEncoder;
import jokrey.utilities.encoder.tag_based.additional.functionality.wrapper.delegation.encryption.aes.AESTagEncryptingBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.LITagBytesEncoder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;

/**
 * Test for the EncryptingBytesEncoder
 *
 * @author jokrey
 */
public class AESEncryptingBytesEncoderTest {
    @Test
    public void simple_comparing_tag_system_test() {
        byte[] key = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};

        LITagBytesEncoder raw_encoder = new LITagBytesEncoder();
        AESContentEncryptingBytesEncoder content_encrypting_encoder = new AESContentEncryptingBytesEncoder(new LITagBytesEncoder(), key);
        AESTagEncryptingBytesEncoder tag_encrypting_encoder = new AESTagEncryptingBytesEncoder(new LITagBytesEncoder(), key);
        AESEncryptingBytesEncoder all_encrypting_encoder = new AESEncryptingBytesEncoder(new LITagBytesEncoder(), key);

        TagSystemTestHelper.enter_values(raw_encoder);
        TagSystemTestHelper.enter_values(content_encrypting_encoder);
        TagSystemTestHelper.enter_values(tag_encrypting_encoder);
        TagSystemTestHelper.enter_values(all_encrypting_encoder);

        //what follows is the worst possible way to check whether or not it has been properly encrypted:
        assertFalse(Arrays.equals(raw_encoder.getEncodedBytes(), content_encrypting_encoder.getEncodedBytes()));
        assertFalse(Arrays.equals(raw_encoder.getEncodedBytes(), tag_encrypting_encoder.getEncodedBytes()));
        assertFalse(Arrays.equals(raw_encoder.getEncodedBytes(), all_encrypting_encoder.getEncodedBytes()));
        assertFalse(Arrays.equals(content_encrypting_encoder.getEncodedBytes(), tag_encrypting_encoder.getEncodedBytes()));
        assertFalse(Arrays.equals(content_encrypting_encoder.getEncodedBytes(), all_encrypting_encoder.getEncodedBytes()));
        assertFalse(Arrays.equals(tag_encrypting_encoder.getEncodedBytes(), all_encrypting_encoder.getEncodedBytes()));

        TagSystemTestHelper.do_tag_system_assertions_without_delete(raw_encoder);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(content_encrypting_encoder);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(tag_encrypting_encoder);
        TagSystemTestHelper.do_tag_system_assertions_without_delete(all_encrypting_encoder);

        TagSystemTestHelper.basic_typed_system_test(raw_encoder);
        TagSystemTestHelper.basic_typed_system_test(content_encrypting_encoder);
        TagSystemTestHelper.basic_typed_system_test(tag_encrypting_encoder);
        TagSystemTestHelper.basic_typed_system_test(all_encrypting_encoder);

        TagSystemTestHelper.do_tag_system_assertions_delete(raw_encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(content_encrypting_encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(tag_encrypting_encoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(all_encrypting_encoder);

        TagSystemTestHelper.do_stream_test(raw_encoder);
        TagSystemTestHelper.do_stream_test(content_encrypting_encoder);
        TagSystemTestHelper.do_stream_test(tag_encrypting_encoder);
        TagSystemTestHelper.do_stream_test(all_encrypting_encoder);
    }
}