package jokrey.utilities.transparent_storage.bytes.wrapper;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;

import java.io.InputStream;
import java.util.Objects;

/**
 * Offers a view into a specific part of the given, underlying storage.
 * 
 * All methods only function within the boundaries set upfront.
 * This method will never leave the underlying storage smaller, only (potentially) larger. But never larger than start+end
 *    Deletion is not possible, use "set" to null values.
 * @author jokrey
 */
public class SubBytesStorage implements TransparentBytesStorage {
    public long start;
    public long end;
    private TransparentBytesStorage delegate;

    public SubBytesStorage(long start, long end, TransparentBytesStorage delegate) {
        if(end < start || start < 0)
            throw new IndexOutOfBoundsException("start("+start+"), end("+end+")");
        this.start = start;
        this.end = end;
        this.delegate = delegate;
    }

    public long end() {
        return Math.min(end, delegate.contentSize());
    }

    @Override public TransparentBytesStorage set(long start, InputStream content, long content_length) throws StorageSystemException {
        if(start + content_length <= end) {
            delegate.append(content, content_length);
            return this;
        }
        throw new IndexOutOfBoundsException("Doesn't fit by "+((start+content_length)-end)+" bytes");
    }


    public SubBytesStorage appendFrom(long start, byte[] part, int off, int len) throws StorageSystemException {
        delegate.set(this.start+start, part, off, len);
        return this;
    }
    @Override public TransparentBytesStorage set(long start, byte[] part, int off, int len) throws StorageSystemException {
        long delegate_size = delegate.contentSize();
        if(delegate_size + part.length <= end) {
            delegate.set(this.start+start, part, off, len);
            return this;
        }
        throw new IndexOutOfBoundsException("Doesn't fit by "+((delegate_size+part.length)-end)+" bytes");
    }

    @Override public TransparentBytesStorage set(long start, byte[] part, int off) throws StorageSystemException {
        return set(start, part, off, part.length-off);
    }

    @Override public TransparentBytesStorage set(long start, byte[] part) throws StorageSystemException {
        return set(start, part, 0);
    }

    @Override public TransparentBytesStorage set(long start, byte part) throws StorageSystemException {
        return set(start, new byte[] {part});
    }

    @Override public InputStream substream(long start, long end) throws StorageSystemException {
        if(end<=this.end)
            return delegate.substream(start-this.start, end-this.start);
        throw new IndexOutOfBoundsException();
    }

    @Override public InputStream stream() {
        return delegate.substream(start, Math.min(end, delegate.contentSize()));
    }

    @Override public byte[] sub(long start, long end) {
        if(end<=this.end)
            return delegate.sub(start-this.start, end-this.start);
        throw new IndexOutOfBoundsException();
    }

    @Override public long contentSize() {
        return Math.min(end, delegate.contentSize()) - start;
    }

    @Override public boolean isEmpty() {
        return start >= Math.min(end, delegate.contentSize());
    }

    @Override public void setContent(byte[] content) {
        set(start, content);
    }

    @Override public byte[] getContent() {
        return delegate.sub(start, Math.min(end, delegate.contentSize()));
    }

    @Override public TransparentBytesStorage copyInto(long start, byte[] b, int off, int len) {
        return delegate.copyInto(this.start + start, b, off, len);
    }

    public void startIndexAdd(int by) {
        start+=by;
    }


    @Override public void clear() {
        throw new UnsupportedOperationException();
    }
    @Override public TransparentBytesStorage delete(long start, long end) throws StorageSystemException {
        throw new UnsupportedOperationException();
    }
    /**
     * Does NOT close underlying storage
     */
    @Override public void close() {
        delegate=null;
    }

    public byte getFirst() {
        return delegate.getByte(start);
    }

    @Override public byte getByte(long index) {
        return delegate.getByte(start + index);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubBytesStorage that = (SubBytesStorage) o;
        return start == that.start &&
                end == that.end &&
                delegate == that.delegate;
    }
    @Override public int hashCode() {
        return Objects.hash(start, end, System.identityHashCode(delegate));
    }

    @Override public String toString() {
        return "SubBytesStorage{" +
                "start=" + start +
                ", end=" + end +
                ", delegate.size=" + delegate.contentSize() +
                '}';
    }
}
