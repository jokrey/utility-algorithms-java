package jokrey.utilities.encoder.tag_based.tests;

import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.LBTagEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.transparent_storage.StorageSystemException;
import org.junit.Test;

/**
 * @author jokrey
 */
public class LBTagEncoderTest {
    @Test
    public void do_tag_system_test() throws StorageSystemException {
        //order, values and type don't matter
        //just keep in mind that EACH tag has to have a different name!!!!


        LBTagEncoder encoder = new LBTagEncoder();
        TagSystemTestHelper.enter_values(encoder);

        byte[] encodedArray = encoder.getEncodedBytes();
        LBTagEncoder decoder = new LBTagEncoder(encodedArray);
//        LBTagEncoder decoder = encoder;


        TagSystemTestHelper.do_tag_system_assertions_without_delete(decoder);
        TagSystemTestHelper.do_tag_system_assertions_delete(decoder);

        TagSystemTestHelper.basic_typed_system_test(encoder);
    }
}
