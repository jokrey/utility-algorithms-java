package jokrey.utilities.transparent_storage.bytes;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.SubStorage;
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
    @Override public SubBytesStorage subStorage(long start) {
        if(start < this.start)
            throw new IndexOutOfBoundsException("start("+start+") < this.start("+this.start+")");
        return new SubBytesStorage((TransparentBytesStorage) delegate, this.start + start, end());
    }
    @Override public SubBytesStorage subStorage(long start, long end) {
        if(start < this.start || end > this.end)
            throw new IndexOutOfBoundsException("start("+start+") < this.start("+this.start+") || end("+end+") > this.end("+this.end+")");
        return new SubBytesStorage((TransparentBytesStorage) delegate, start, end);
    }
    @Override public SubBytesStorage[] split(long at) {
        long split_start = start + at;
        long split_end = end();
        if(split_start > split_end)
            throw new IndexOutOfBoundsException("split_start("+split_start+") > split_end("+split_end+")");
        return new SubBytesStorage[] {
                new SubBytesStorage((TransparentBytesStorage) delegate, 0, split_start),
                new SubBytesStorage((TransparentBytesStorage) delegate, split_start, split_end)
        };
    }
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
