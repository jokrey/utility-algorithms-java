package jokrey.utilities.encoder.tag_based.tests.serialization.full;

/**
 * @author jokrey
 */
public class TestFullObjectSuper {
    private int test;
    private final long asdasd;
//    private short does_not_work = 234;
    public TestFullObjectSuper() {
        asdasd=12;
    }
    public TestFullObjectSuper(int test, long asdasd) {
        this.test = test;
        this.asdasd = asdasd;
    }



    @Override public boolean equals(Object o) {
        return o instanceof TestFullObjectSuper &&
                test == ((TestFullObjectSuper)o).test &&
                asdasd == ((TestFullObjectSuper)o).asdasd;
    }

    @Override public String toString() {
        return "[TestFullObjectSuper: " +
                "test: \""+test+"\", " +
                "asdasd: \""+asdasd+"\", "+"\"]";
    }
}
