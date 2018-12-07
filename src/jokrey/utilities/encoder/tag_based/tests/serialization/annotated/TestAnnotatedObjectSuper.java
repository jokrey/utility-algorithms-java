package jokrey.utilities.encoder.tag_based.tests.serialization.annotated;

import jokrey.utilities.encoder.tag_based.serialization.field.annotated.Encode;

/**
 * @author jokrey
 */
public class TestAnnotatedObjectSuper {
    @Encode
    private int test = -1234;
    @Encode
    private long asdasd = 5829875469873549873L;
    public TestAnnotatedObjectSuper() {
    }
    public TestAnnotatedObjectSuper(int test, long asdasd) {
        this.test = test;
        this.asdasd = asdasd;
    }



    @Override public boolean equals(Object o) {
        return o instanceof TestAnnotatedObjectSuper &&
                test == ((TestAnnotatedObjectSuper)o).test &&
                asdasd == ((TestAnnotatedObjectSuper)o).asdasd;
    }

    @Override public String toString() {
        return "[TestFullObjectSuper: " +
                "test: \""+test+"\", " +
                "asdasd: \""+asdasd+"\", "+"\"]";
    }
}
