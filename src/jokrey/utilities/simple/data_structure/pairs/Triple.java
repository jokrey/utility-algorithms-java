package jokrey.utilities.simple.data_structure.pairs;

import java.util.Objects;

/**
 * Generic pair.
 * Immutable when and only when R and L are immutable.
 */
public class Triple<L,M,R> {
    public final R r;
    public final M m;
    public final L l;

    public Triple(L left, M middle, R right) {
        r = right;
        m = middle;
        l = left;
    }

    public L getLeft() {
        return l;
    }
    public M getMiddle() {
        return m;
    }
    public R getRight() {
        return r;
    }

    public L getFirst() {
        return l;
    }
    public M getSecond() {
        return m;
    }
    public R getThird() {
        return r;
    }

    public L getKey() {
        return l;
    }
    public M getValue1() {
        return m;
    }
    public R getValue2() {
        return r;
    }

    @Override public int hashCode() { return (((l.hashCode()*13) ^ r.hashCode())*13) ^ m.hashCode(); }
    @Override public boolean equals(Object obj) {
        if (obj instanceof Triple) {
            Triple o = (Triple) obj;
            return Objects.equals(l, o.l) && Objects.equals(m, o.m) && Objects.equals(r, o.r);
        }
        return false;
    }
    @Override public String toString() {
        return "[Triple: l=\""+l+"\", m=\""+m+"\", r=\""+r+"\"]";
    }
}