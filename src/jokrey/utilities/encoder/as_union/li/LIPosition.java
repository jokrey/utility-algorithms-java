package jokrey.utilities.encoder.as_union.li;

import jokrey.utilities.encoder.as_union.Position;
import jokrey.utilities.transparent_storage.TransparentStorage;

import java.util.Objects;

/**
 * @author jokrey
 */
public class LIPosition extends Position {
    public long pointer;
    public LIPosition(long pointer) {
        this.pointer = pointer;
    }
    @Override public boolean hasNext(TransparentStorage storage) {
        return pointer < storage.contentSize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LIPosition that = (LIPosition) o;
        return pointer == that.pointer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointer);
    }

    @Override
    public String toString() {
        return "LIPosition{" +
                "pointer=" + pointer +
                '}';
    }
}
