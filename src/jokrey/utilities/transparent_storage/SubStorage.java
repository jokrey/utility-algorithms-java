package jokrey.utilities.transparent_storage;

import jokrey.utilities.transparent_storage.string.non_persistent.SubStringStorage;

import java.util.Objects;

/**
 * Offers a view into a specific part of the given, underlying storage.
 *
 * All methods only function within the boundaries set upfront.
 * This method will never leave the underlying storage smaller, only (potentially) larger. But never larger than start+end
 *    Deletion is not possible because that would change other parts of the delegated storage as well, use "set" to null values.
 * @author jokrey
 */
public class SubStorage<SF> implements TransparentStorage<SF> {
    protected TransparentStorage<SF> delegate;
    public long start;
    public long end;

    /**
     * @param delegate delegated storage
     * @param start inclusive
     * @param end inclusive
     */
    public SubStorage(TransparentStorage<SF> delegate, long start, long end) {
        this.delegate = delegate;
        this.start = start;
        this.end = end;

        if(start > end)
            throw new IllegalArgumentException("start("+start+") > end("+end+")");
        if(start < 0)
            throw new IndexOutOfBoundsException("start("+start+") < 0");
        if(delegate==null)
            throw new NullPointerException("delegate==null");
    }


    public long end() {
        return Math.min(end, delegate.contentSize());
    }

    @Override public int len(SF part) {
        return delegate.len(part);
    }

    @Override public SF sub(long start, long end) {
        if(end<=this.end)
            return delegate.sub(start-this.start, end-this.start);
        throw new IndexOutOfBoundsException();
    }

    @Override public SubStorage<SF> set(long start, SF part, int off, int len) throws StorageSystemException {
        long delegate_size = delegate.contentSize();
        if(delegate_size + len(part) <= end) {
            delegate.set(this.start+start, part, off, len);
            return this;
        }
        throw new IndexOutOfBoundsException("Doesn't fit by "+((delegate_size+len(part))-end)+" bytes");
    }

    @Override public SubStorage<SF> set(long start, SF part, int off) throws StorageSystemException {
        return set(start, part, off, len(part)-off);
    }

    @Override public SubStorage<SF> set(long start, SF part) throws StorageSystemException {
        return set(start, part, 0);
    }

    @Override public long contentSize() {
        return Math.min(end, delegate.contentSize()) - start;
    }

    @Override public boolean isEmpty() {
        return start >= Math.min(end, delegate.contentSize());
    }

    @Override public void setContent(SF content) {
        set(start, content);
    }

    @Override public SF getContent() {
        return delegate.sub(start, Math.min(end, delegate.contentSize()));
    }



    @Override public SubStorage<SF> subStorage(long start) {
        if(start < this.start)
            throw new IndexOutOfBoundsException("start("+start+") < this.start("+this.start+")");
        return new SubStorage<>(delegate, this.start + start, end());
    }
    @Override public SubStorage<SF> subStorage(long start, long end) {
        if(start < this.start || end > this.end)
            throw new IndexOutOfBoundsException("start("+start+") < this.start("+this.start+") || end("+end+") > this.end("+this.end+")");
        return new SubStorage<>(delegate, start, end);
    }
    @Override public SubStorage<SF>[] split(long at) {
        long split_start = start + at;
        long split_end = end();
        if(split_start > split_end)
            throw new IndexOutOfBoundsException("split_start("+split_start+") > split_end("+split_end+")");
        return new SubStorage[] {
                new SubStorage<>(delegate, 0, split_start),
                new SubStorage<>(delegate, split_start, split_end)
        };
    }

    public void startIndexAdd(int by) {
        start+=by;
    }


    @Override public void clear() {
        throw new UnsupportedOperationException();
    }
    @Override public SubStorage<SF> delete(long start, long end) throws StorageSystemException {
        throw new UnsupportedOperationException();
    }
    /**
     * Does NOT close underlying storage
     */
    @Override public void close() {
        delegate=null;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubStorage<?> that = (SubStorage<?>) o;
        return start == that.start &&
                end == that.end &&
                delegate == that.delegate;
    }
    @Override public int hashCode() {
        return Objects.hash(start, end, System.identityHashCode(delegate));
    }

    @Override public String toString() {
        return "SubStorage<SF>{" +
                "start=" + start +
                ", end=" + end +
                ", delegate.size=" + delegate.contentSize() +
                '}';
    }

    public SubStorage<SF> combineWith(SubStorage<SF> directlyFollowing) {
        if(this.end() != directlyFollowing.start)
            throw new IllegalArgumentException("this.end()("+this.end()+") != directlyFollowing.start("+directlyFollowing.start+")");
        if(delegate != directlyFollowing.delegate)
            throw new IllegalArgumentException("delegate != directlyFollowing.delegate");
        return new SubStorage<>(delegate, this.start, directlyFollowing.end);
    }
}
