package jokrey.utilities.encoder.tag_based.tests.serialization.beanish;

import jokrey.utilities.encoder.EncodableAsBytes;
import jokrey.utilities.encoder.EncodableAsString;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.serialization.LIObjectEncoderBeanish;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.string.LITagStringEncoder;
import jokrey.utilities.encoder.tag_based.serialization.beanish.ObjectEncoderBeanish;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jokrey
 */
public class ObjectEncoderBeanishTest {
    private TestBeanObject o = TestBeanObject.getRandomizedExample();

    @Test
    public void bean_serialize_test_en_use__de_use() throws InstantiationException, IllegalAccessException {
        bean_serializer_test_helper(LITagStringEncoder.class, true);

        String e = LIObjectEncoderBeanish.serialize_as_str(o);
        TestBeanObject r = LIObjectEncoderBeanish.deserialize(e, TestBeanObject.class);
        assertEquals(o, r);
    }
    @Test
    public void bean_serialize_test_en_litbe__de_use() throws InstantiationException, IllegalAccessException {
        bean_serializer_test_helper(LITagBytesEncoder.class, true);
    }
    @Test
    public void bean_serialize_test_en_use__de_litbe() throws InstantiationException, IllegalAccessException {
        bean_serializer_test_helper(LITagStringEncoder.class, false);
    }
    @Test
    public void bean_serialize_test_en_litbe__de_litbe() throws InstantiationException, IllegalAccessException {
        bean_serializer_test_helper(LITagBytesEncoder.class, false);

        byte[] e = LIObjectEncoderBeanish.serialize(o);
        TestBeanObject r = LIObjectEncoderBeanish.deserialize(e, TestBeanObject.class);
        assertEquals(o, r);
    }

    private void bean_serializer_test_helper(Class<? extends TagBasedEncoder> encoder_class_used, boolean decode_string) throws IllegalAccessException, InstantiationException {
        TagBasedEncoder encoder = ObjectEncoderBeanish.serialize(encoder_class_used.newInstance(), o);

        if(decode_string) {
            TagBasedEncoder decoder = EncodableAsString.createFromEncodedString(encoder.getEncodedString(), encoder_class_used);
            TestBeanObject r = ObjectEncoderBeanish.deserialize(decoder, TestBeanObject.class);
            assertEquals(o, r);
        } else {
            TagBasedEncoder decoder = EncodableAsBytes.createFromEncodedBytes(encoder.getEncodedBytes(), encoder_class_used);
            TestBeanObject r = ObjectEncoderBeanish.deserialize(decoder, TestBeanObject.class);
            assertEquals(o, r);
        }
    }
}
