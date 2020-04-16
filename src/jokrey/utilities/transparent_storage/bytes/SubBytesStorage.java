package jokrey.utilities.transparent_storage.bytes;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.SubStorage;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;

import java.io.InputStream;

public class SubBytesStorage extends SubStorage<byte[]> implements TransparentBytesStorage {
    public SubBytesStorage(TransparentBytesStorage delegate, long end, long start) {
        super(delegate, start, end);
    }

    public static SubBytesStorage empty() {
        return new SubBytesStorage(new ByteArrayStorage(0), 0, 0);
    }

    public byte getFirst() {
        return ((TransparentBytesStorage)delegate).getByte(start);
    }

    @Override public byte getByte(long index) {
        if(start+index > end)
            throw new IndexOutOfBoundsException();
        return ((TransparentBytesStorage)delegate).getByte(start + index);
    }
    @Override public TransparentBytesStorage set(long start, InputStream content, long content_length) throws StorageSystemException {
        if(start + content_length <= end) {
            ((TransparentBytesStorage)delegate).append(content, content_length);
            return this;
        }
        throw new IndexOutOfBoundsException("Doesn't fit by "+((start+content_length)-end)+" bytes");
    }

    @Override public InputStream substream(long start, long end) throws StorageSystemException {
        if(end<=this.end)
            return ((TransparentBytesStorage)delegate).substream(start-this.start, end-this.start);
        throw new IndexOutOfBoundsException();
    }

    @Override public InputStream stream() {
        return ((TransparentBytesStorage)delegate).substream(start, Math.min(end, delegate.contentSize()));
    }

    @Override public TransparentBytesStorage copyInto(long start, byte[] b, int off, int len) {
        return ((TransparentBytesStorage)delegate).copyInto(this.start + start, b, off, len);
    }



    //OVERRIDES TO CORRECT RETURN TYPE
    public SubBytesStorage delete(long start, long end) throws StorageSystemException {
        super.delete(start, end);
        return this;
    }
    public SubBytesStorage set(long start, byte[] part, int off, int len) throws StorageSystemException {
        super.set(start, part, off, len);
        return this;
    }
    public SubBytesStorage set(long start, byte[] part, int off) throws StorageSystemException {
        super.set(start, part, off);
        return this;
    }
    public SubBytesStorage set(long start, byte[] part) throws StorageSystemException {
        return set(start, part, 0);
    }
    @Override public TransparentBytesStorage set(long start, byte part) throws StorageSystemException {
        set(start, new byte[] {part});
        return this;
    }
    public SubBytesStorage append(byte[] val) throws StorageSystemException {
        return set(contentSize(), val);
    }
}
