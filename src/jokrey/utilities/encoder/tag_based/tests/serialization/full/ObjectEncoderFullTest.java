package jokrey.utilities.encoder.tag_based.tests.serialization.full;

import jokrey.utilities.encoder.tag_based.EncodableAsBytes;
import jokrey.utilities.encoder.tag_based.EncodableAsString;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.serialization.LIObjectEncoderFull;
import jokrey.utilities.encoder.tag_based.implementation.length_indicator.string.LITagStringEncoder;
import jokrey.utilities.encoder.tag_based.serialization.field.full.ObjectEncoderFull;
import jokrey.utilities.encoder.tag_based.tests.serialization.beanish.TestBeanObject;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author jokrey
 */
public class ObjectEncoderFullTest {
    private TestFullObject o = new TestFullObject(-1233, 945783945789866557L, TestBeanObject.getRandomizedExample(), true);

    @Test
    public void full_serialize_test_en_use__de_use() throws InstantiationException, IllegalAccessException {
        full_serializer_test_helper(LITagStringEncoder.class, true);
        full_serializer_test_helper(LITagStringEncoder.class, o);
        full_serializer_test_helper(LITagStringEncoder.class, "string test");
        full_serializer_test_helper(LITagStringEncoder.class, 17357);
        full_serializer_test_helper(LITagStringEncoder.class, new String[][] {{""}, {}, {"test", "hello"}});

        String e = LIObjectEncoderFull.serialize_as_str(o);
        TestFullObject r = LIObjectEncoderFull.deserialize(e, TestFullObject.class);
        assertEquals(o, r);
    }
    @Test
    public void full_serialize_test_en_litbe__de_use() throws InstantiationException, IllegalAccessException {
        full_serializer_test_helper(LITagBytesEncoder.class, true);
    }
    @Test
    public void full_serialize_test_en_use__de_litbe() throws InstantiationException, IllegalAccessException {
        full_serializer_test_helper(LITagStringEncoder.class, false);
    }
    @Test
    public void full_serialize_test_en_litbe__de_litbe() throws InstantiationException, IllegalAccessException {
        full_serializer_test_helper(LITagBytesEncoder.class, false);
        full_serializer_test_helper(LITagBytesEncoder.class, o);
        full_serializer_test_helper(LITagStringEncoder.class, o);
        full_serializer_test_helper(LITagStringEncoder.class, "string test");
        full_serializer_test_helper(LITagStringEncoder.class, 17357);
        full_serializer_test_helper(LITagStringEncoder.class, new String[][] {{""}, {}, {"test", "hello"}});

        byte[] e = LIObjectEncoderFull.serialize(o);
        TestFullObject r = LIObjectEncoderFull.deserialize(e, TestFullObject.class);
        assertEquals(o, r);
    }

    private void full_serializer_test_helper(Class<? extends TagBasedEncoder> encoder_class_used, boolean decode_string) throws IllegalAccessException, InstantiationException {
        TagBasedEncoder encoder = ObjectEncoderFull.serialize(encoder_class_used.newInstance(), o);

        if(decode_string) {
            TagBasedEncoder decoder = EncodableAsString.createFromEncodedString(encoder.getEncodedString(), encoder_class_used);
            TestFullObject r = ObjectEncoderFull.deserialize(decoder, TestFullObject.class);
            assertEquals(o, r);
        } else {
            TagBasedEncoder decoder = EncodableAsBytes.createFromEncodedBytes(encoder.getEncodedBytes(), encoder_class_used);
            TestFullObject r = ObjectEncoderFull.deserialize(decoder, TestFullObject.class);
            assertEquals(o, r);
        }
    }

    @SuppressWarnings("unchecked")
    private<SF> void full_serializer_test_helper(Class<? extends TagBasedEncoder<SF>> encoder_class_used, Object orig) throws IllegalAccessException, InstantiationException {
        SF encoded = (SF) ObjectEncoderFull.serialize(encoder_class_used, orig).getEncoded();

        Object r = ObjectEncoderFull.deserialize(encoded, encoder_class_used, orig.getClass());
        try {
            assertEquals(orig, r);
        } catch(AssertionError e) {
            try {
                assertArrayEquals((Object[]) orig, (Object[]) r);
            } catch(AssertionError e2) {
                e.printStackTrace();
                throw e2;
            }
        }
    }
}
