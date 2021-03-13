package jokrey.utilities.encoder.as_union.li;

import jokrey.utilities.encoder.as_union.Position;
import jokrey.utilities.transparent_storage.TransparentStorage;

import java.util.Objects;

/**
 * @author jokrey
 */
public class ReverseLIPosition extends Position {
    public final long minimum;
    public long pointer;
    public ReverseLIPosition(long pointer, long minimum) {
        this.pointer = pointer;
        this.minimum = minimum;
    }
    @Override public boolean hasNext(TransparentStorage storage) {
        return pointer > minimum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReverseLIPosition that = (ReverseLIPosition) o;
        return minimum == that.minimum && pointer == that.pointer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, pointer);
    }

    @Override
    public String toString() {
        return "ReverseLIPosition{" +
                "minimum=" + minimum +
                ", pointer=" + pointer +
                '}';
    }
}
