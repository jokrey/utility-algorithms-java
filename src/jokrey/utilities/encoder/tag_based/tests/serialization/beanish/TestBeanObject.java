package jokrey.utilities.encoder.tag_based.tests.serialization.beanish;

import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.encoder.as_union.li.string.LIse;

import java.util.Arrays;
import java.util.Random;

/**
 * Test Object, following java bean convention.
 * Java doc not required.
 * @author jokrey
 */
public class TestBeanObject {
    private String orig_1;
    private Boolean orig_2;
    private Byte orig_3;
    private Integer orig_4;
    private Long orig_5;
    private boolean orig_6;
    private byte orig_7;
    private int orig_8;
    private long orig_9;
    private String[] orig_10;
    private boolean[] orig_11;
    private byte[] orig_12;
    private int[] orig_13;
    private long[] orig_14;
    private String[][] orig_15;
    private LIse orig_16;
    private LIbae orig_17;
//    private EncodableAsBytes orig_16; //does not work. The concrete Type implementing EncodableAsBytes has to be known
//    private EncodableAsString orig_17; //does not work. The concrete Type implementing EncodableAsString has to be known

    public TestBeanObject() {}
    public TestBeanObject(String orig_1, Boolean orig_2, Byte orig_3, Integer orig_4, Long orig_5,
                          boolean orig_6, byte orig_7, int orig_8, long orig_9, String[] orig_10,
                          boolean[] orig_11, byte[] orig_12, int[] orig_13, long[] orig_14, String[][] orig_15,
                          LIse orig_16, LIbae orig_17) {
        this.orig_1=orig_1; this.orig_2 = orig_2;
        this.orig_3 = orig_3; this.orig_4 = orig_4;
        this.orig_5 = orig_5; this.orig_6 = orig_6;
        this.orig_7 = orig_7; this.orig_8 = orig_8;
        this.orig_9 = orig_9; this.orig_10 = orig_10;
        this.orig_11 = orig_11; this.orig_12 = orig_12;
        this.orig_13 = orig_13; this.orig_14 = orig_14;
        this.orig_15 = orig_15; this.orig_16 = orig_16;
        this.orig_17 = orig_17;
    }

    public static TestBeanObject getRandomizedExample() {
        Random r = new Random();
        byte[] bytes = new byte[r.nextInt(6)+2];
        r.nextBytes(bytes);
        return new TestBeanObject(
                "qwert"+r.nextInt(), r.nextBoolean(), bytes[0], r.nextInt(), r.nextLong(), r.nextBoolean(), bytes[1], r.nextInt(), r.nextLong(),
                new String[]{"moin, "+r.nextInt(), "fertig"+r.nextInt()}, new boolean[] {r.nextBoolean(), false, r.nextBoolean(), r.nextBoolean(), r.nextBoolean()},
                bytes, new int[] {r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt()}, new long[] {r.nextLong(), r.nextLong(), r.nextLong()},
                new String[][]{{"Hall "+r.nextInt(), "dies", "ist"+r.nextInt()}, {"Ein"+r.nextInt(), "Test"}}, new LIse("moin"+r.nextInt(), "we", "are", "done", "it's 4am"+r.nextInt()), new LIbae());
    }


    @Override public boolean equals(Object o) {
        return o instanceof TestBeanObject &&
                orig_1.equals(((TestBeanObject)o).orig_1) &&
                orig_2 == ((TestBeanObject)o).orig_2 &&
                orig_3.equals(((TestBeanObject) o).orig_3) &&
                orig_4.equals(((TestBeanObject) o).orig_4) &&
                orig_5.equals(((TestBeanObject) o).orig_5) &&
                orig_6 == ((TestBeanObject)o).orig_6 &&
                orig_7 == ((TestBeanObject)o).orig_7 &&
                orig_8 == ((TestBeanObject)o).orig_8 &&
                orig_9 == ((TestBeanObject)o).orig_9 &&
                Arrays.equals(orig_10, ((TestBeanObject)o).orig_10) &&
                Arrays.equals(orig_11, ((TestBeanObject)o).orig_11) &&
                Arrays.equals(orig_12, ((TestBeanObject)o).orig_12) &&
                Arrays.equals(orig_13, ((TestBeanObject)o).orig_13) &&
                Arrays.equals(orig_14, ((TestBeanObject)o).orig_14) &&
                Arrays.deepEquals(orig_15, ((TestBeanObject)o).orig_15) &&
                orig_16.equals(((TestBeanObject)o).orig_16) &&
                orig_17.equals(((TestBeanObject)o).orig_17);
    }




    public String getOrig_1() {
        return orig_1;
    }

    public void setOrig_1(String orig_1) {
        this.orig_1 = orig_1;
    }

    public Boolean getOrig_2() {
        return orig_2;
    }

    public void setOrig_2(Boolean orig_2) {
        this.orig_2 = orig_2;
    }

    public Byte getOrig_3() {
        return orig_3;
    }

    public void setOrig_3(Byte orig_3) {
        this.orig_3 = orig_3;
    }

    public Integer getOrig_4() {
        return orig_4;
    }

    public void setOrig_4(Integer orig_4) {
        this.orig_4 = orig_4;
    }

    public Long getOrig_5() {
        return orig_5;
    }

    public void setOrig_5(Long orig_5) {
        this.orig_5 = orig_5;
    }

    public boolean getOrig_6() {
        return orig_6;
    }

    public void setOrig_6(boolean orig_6) {
        this.orig_6 = orig_6;
    }

    public byte getOrig_7() {
        return orig_7;
    }

    public void setOrig_7(byte orig_7) {
        this.orig_7 = orig_7;
    }

    public int getOrig_8() {
        return orig_8;
    }

    public void setOrig_8(int orig_8) {
        this.orig_8 = orig_8;
    }

    public long getOrig_9() {
        return orig_9;
    }

    public void setOrig_9(long orig_9) {
        this.orig_9 = orig_9;
    }

    public String[] getOrig_10() {
        return orig_10;
    }

    public void setOrig_10(String[] orig_10) {
        this.orig_10 = orig_10;
    }

    public boolean[] getOrig_11() {
        return orig_11;
    }

    public void setOrig_11(boolean[] orig_11) {
        this.orig_11 = orig_11;
    }

    public byte[] getOrig_12() {
        return orig_12;
    }

    public void setOrig_12(byte[] orig_12) {
        this.orig_12 = orig_12;
    }

    public int[] getOrig_13() {
        return orig_13;
    }

    public void setOrig_13(int[] orig_13) {
        this.orig_13 = orig_13;
    }

    public long[] getOrig_14() {
        return orig_14;
    }

    public void setOrig_14(long[] orig_14) {
        this.orig_14 = orig_14;
    }

    public String[][] getOrig_15() {
        return orig_15;
    }

    public void setOrig_15(String[][] orig_15) {
        this.orig_15 = orig_15;
    }

    public LIse getOrig_16() {
        return orig_16;
    }

    public void setOrig_16(LIse orig_16) {
        this.orig_16 = orig_16;
    }

    public LIbae getOrig_17() {
        return orig_17;
    }

    public void setOrig_17(LIbae orig_17) {
        this.orig_17 = orig_17;
    }


    @Override public String toString() {
        return "[TestBeanObject: " +
                "1: \""+getOrig_1()+"\", " +
                "2: \""+getOrig_2()+"\", " +
                "3: \""+getOrig_3()+"\", " +
                "4: \""+getOrig_4()+"\", " +
                "5: \""+getOrig_5()+"\", " +
                "6: \""+getOrig_6()+"\", " +
                "7: \""+getOrig_7()+"\", " +
                "8: \""+getOrig_8()+"\", " +
                "9: \""+getOrig_9()+"\", " +
                "10: \""+ Arrays.toString(getOrig_10()) +"\", " +
                "11: \""+ Arrays.toString(getOrig_11()) +"\", " +
                "12: \""+ Arrays.toString(getOrig_12()) +"\", " +
                "13: \""+ Arrays.toString(getOrig_13()) +"\", " +
                "14: \""+ Arrays.toString(getOrig_14()) +"\", " +
                "15: \""+ Arrays.deepToString(getOrig_15()) +"\", " +
                "16: \""+getOrig_16()+"\", " +
                "17: \""+getOrig_17()+"\"]";
    }
}
