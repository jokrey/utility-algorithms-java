package jokrey.utilities.encoder.tag_based.tests.serialization.full;

import jokrey.utilities.encoder.tag_based.tests.serialization.beanish.TestBeanObject;

/**
 * @author jokrey
 */
public class TestFullObject extends TestFullObjectSuper {
    private TestBeanObject bean1; //recursive test
    private boolean b1;
//    private EncodableAsBytes orig_16; //does not work. The concrete Type implementing EncodableAsBytes has to be known
//    private EncodableAsString orig_17; //does not work. The concrete Type implementing EncodableAsString has to be known

    public TestFullObject() {
        super();
    }
    public TestFullObject(int i, long l, TestBeanObject bean1, boolean b1) {
        super(i, l);
        this.bean1=bean1;
        this.b1 = b1;
    }



    @Override public boolean equals(Object o) {
        return super.equals(o) && o instanceof TestFullObject &&
                bean1.equals(((TestFullObject)o).bean1) &&
                b1 == ((TestFullObject)o).b1;
    }

    @Override public String toString() {
        return "[TestFullObject: " +
               "super: \""+super.toString()+"\", " +
                "1: \""+bean1+"\", " +
                "2: \""+b1+"\", "+"\"]";
    }
}
