package jokrey.utilities.encoder.tag_based.tests.serialization.annotated;

import jokrey.utilities.encoder.EncodableAsBytes;
import jokrey.utilities.encoder.EncodableAsString;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.serialization.LIObjectEncoderAnnotation;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.string.LITagStringEncoder;
import jokrey.utilities.encoder.as_union.li.string.LIse;
import jokrey.utilities.encoder.tag_based.serialization.field.annotated.ObjectEncoderAnnotation;
import jokrey.utilities.encoder.tag_based.tests.serialization.beanish.TestBeanObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jokrey
 */
public class ObjectEncoderAnnotationTest {
    private TestAnnotatedObject o = new TestAnnotatedObject(TestBeanObject.getRandomizedExample(), true, (short) 12342, new LIbae(), new LIse());

    @Test
    public void serialize_test_en_use__de_use() throws InstantiationException, IllegalAccessException {
        serializer_test_helper(LITagStringEncoder.class, true);

        String e = LIObjectEncoderAnnotation.serialize_as_str(o);
        TestAnnotatedObject r = LIObjectEncoderAnnotation.deserialize(e, TestAnnotatedObject.class);
        assertEquals(o, r);
    }
    @Test
    public void serialize_test_en_litbe__de_use() throws InstantiationException, IllegalAccessException {
        serializer_test_helper(LITagBytesEncoder.class, true);
    }
    @Test
    public void serialize_test_en_use__de_litbe() throws InstantiationException, IllegalAccessException {
        serializer_test_helper(LITagStringEncoder.class, false);
    }
    @Test
    public void serialize_test_en_litbe__de_litbe() throws InstantiationException, IllegalAccessException {
        serializer_test_helper(LITagBytesEncoder.class, false);

        byte[] e = LIObjectEncoderAnnotation.serialize(o);
        TestAnnotatedObject r = LIObjectEncoderAnnotation.deserialize(e, TestAnnotatedObject.class);
        assertEquals(o, r);
    }

    private void serializer_test_helper(Class<? extends TagBasedEncoder> encoder_class_used, boolean decode_string) throws IllegalAccessException, InstantiationException {
        TagBasedEncoder encoder = ObjectEncoderAnnotation.serialize(encoder_class_used.newInstance(), o);

        if(decode_string) {
            TagBasedEncoder decoder = EncodableAsString.createFromEncodedString(encoder.getEncodedString(), encoder_class_used);
            TestAnnotatedObject r = ObjectEncoderAnnotation.deserialize(decoder, TestAnnotatedObject.class);
            assertEquals(o, r);
        } else {
            TagBasedEncoder decoder = EncodableAsBytes.createFromEncodedBytes(encoder.getEncodedBytes(), encoder_class_used);
            TestAnnotatedObject r = ObjectEncoderAnnotation.deserialize(decoder, TestAnnotatedObject.class);
            assertEquals(o, r);
        }
    }
}
