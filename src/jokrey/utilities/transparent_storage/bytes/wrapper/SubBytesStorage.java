package jokrey.utilities.transparent_storage.bytes.wrapper;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;

import java.io.InputStream;

/**
 * Offers a view into a specific part of the given, underlying storage.
 * 
 * All methods only function within the boundaries set upfront.
 * This method will never leave the underlying storage smaller, only (potentially) larger. But never larger than start+end
 *    Deletion is not possible, use "set" to null values.
 * @author jokrey
 */
public class SubBytesStorage implements TransparentBytesStorage {
    private final long start;
    private final long end;
    private TransparentBytesStorage delegate;

    public SubBytesStorage(long start, long end, TransparentBytesStorage delegate) {
        if(end <= start || start < 0)
            throw new IndexOutOfBoundsException();
        this.start = start;
        this.end = end;
        this.delegate = delegate;
    }

    @Override public TransparentBytesStorage set(long start, InputStream content, long content_length) throws StorageSystemException {
        if(start + content_length <= end) {
            delegate.append(content, content_length);
            return this;
        }
        throw new IndexOutOfBoundsException("Doesn't fit by "+((start+content_length)-end)+" bytes");
    }

    @Override public TransparentBytesStorage set(long start, byte[] val) throws StorageSystemException {
        long delegate_size = delegate.contentSize();
        if(delegate_size + val.length <= end) {
            delegate.append(val);
            return this;
        }
        throw new IndexOutOfBoundsException("Doesn't fit by "+((delegate_size+val.length)-end)+" bytes");
    }

    @Override public InputStream substream(long start, long end) throws StorageSystemException {
        if(end<=this.end)
            return delegate.substream(start-this.start, end-this.start);
        throw new IndexOutOfBoundsException();
    }

    @Override public InputStream stream() {
        return delegate.substream(start, end);
    }

    @Override public byte[] sub(long start, long end) {
        if(end<=this.end)
            return delegate.sub(start-this.start, end-this.start);
        throw new IndexOutOfBoundsException();
    }

    @Override public long contentSize() {
        long delegate_size = delegate.contentSize();
        return delegate_size < end?delegate_size-start:end-start;
    }

    @Override public void setContent(byte[] content) {
        set(start, content);
    }

    @Override public byte[] getContent() {
        return delegate.sub(start, end);
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
}
