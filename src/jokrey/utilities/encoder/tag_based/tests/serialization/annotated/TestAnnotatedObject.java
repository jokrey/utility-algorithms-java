package jokrey.utilities.encoder.tag_based.tests.serialization.annotated;

import jokrey.utilities.encoder.EncodableAsBytes;
import jokrey.utilities.encoder.EncodableAsString;
import jokrey.utilities.encoder.tag_based.serialization.field.annotated.Encode;
import jokrey.utilities.encoder.tag_based.tests.serialization.beanish.TestBeanObject;

import java.util.Arrays;

/**
 * @author jokrey
 */
public class TestAnnotatedObject extends TestAnnotatedObjectSuper {
    @Encode
    private TestBeanObject bean1; //recursive test
    @Encode
    private boolean b1;
//    @Encode
    private short would_break_everything_if_included_1 = 234;
//    @Encode
    private EncodableAsBytes would_break_everything_if_included_2; //does not work. The concrete Type implementing EncodableAsBytes has to be known
//    @Encode
    private EncodableAsString would_break_everything_if_included_3; //does not work. The concrete Type implementing EncodableAsString has to be known

    public TestAnnotatedObject() {
        super();
    }
    public TestAnnotatedObject(TestBeanObject bean1, boolean b1, short would_break_everything_if_included_1, EncodableAsBytes would_break_everything_if_included_2, EncodableAsString would_break_everything_if_included_3) {
        super();
        this.bean1=bean1;
        this.b1 = b1;
        this.would_break_everything_if_included_1=would_break_everything_if_included_1;
        this.would_break_everything_if_included_2=would_break_everything_if_included_2;
        this.would_break_everything_if_included_3=would_break_everything_if_included_3;
    }



    @Override public boolean equals(Object o) {
        return super.equals(o) && o instanceof TestAnnotatedObject &&
                bean1.equals(((TestAnnotatedObject)o).bean1) &&
                b1 == ((TestAnnotatedObject)o).b1;
    }

    @Override public String toString() {
        return "[TestFullObject: " +
               "super: \""+super.toString()+"\", " +
                "1: \""+bean1+"\", " +
                "2: \""+b1+"\", "+"\", " +
                "would_break_everything_if_included_1: \""+would_break_everything_if_included_1+"\", "+"\", " +
                "would_break_everything_if_included_2: \""+ Arrays.toString(would_break_everything_if_included_2.getEncodedBytes()) +"\", "+"\", " +
                "would_break_everything_if_included_3: \""+would_break_everything_if_included_3.getEncodedString()+"\", "+"\"" +
                "]";
    }
}
