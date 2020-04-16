package jokrey.utilities.transparent_storage.string.non_persistent;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.SubStorage;

import java.util.ArrayList;

public class SubStringStorage extends SubStorage<String> implements StringStorage {
    public SubStringStorage(StringStorage delegate, long end, long start) {
        super(delegate, start, end);
    }

    public static SubStringStorage empty() {
        return new SubStringStorage(new StringStorageSystem(0), 0, 0);
    }

    @Override public long indexOf(String s, long from) {
        if(start+from > end) throw new IndexOutOfBoundsException();
        long result = ((StringStorage) delegate).indexOf(s, start+from);
        if(result > end) return -1;
        return result;
    }
    @Override public long lastIndexOf(String s, long backwardsFrom) {
        if(start+backwardsFrom > end) throw new IndexOutOfBoundsException();
        long result = ((StringStorage) delegate).lastIndexOf(s, start+backwardsFrom);
        if(result < start) return -1;
        return result;
    }


    //OVERRIDE TO CORRECT RETURN TYPE
    @Override public SubStringStorage subStorage(long start) {
        if(start < this.start)
            throw new IndexOutOfBoundsException("start("+start+") < this.start("+this.start+")");
        return new SubStringStorage((StringStorage) delegate, this.start + start, end());
    }
    @Override public SubStringStorage subStorage(long start, long end) {
        if(start < this.start || end > this.end)
            throw new IndexOutOfBoundsException("start("+start+") < this.start("+this.start+") || end("+end+") > this.end("+this.end+")");
        return new SubStringStorage((StringStorage) delegate, start, end);
    }
    @Override public SubStringStorage[] split(long at) {
        long split_start = start + at;
        long split_end = end();
        if(split_start > split_end)
            throw new IndexOutOfBoundsException("split_start("+split_start+") > split_end("+split_end+")");
        return new SubStringStorage[] {
                new SubStringStorage((StringStorage) delegate, 0, split_start),
                new SubStringStorage((StringStorage) delegate, split_start, split_end)
        };
    }
    public SubStringStorage combineWith(SubStringStorage directlyFollowing) {
        if(this.end() != directlyFollowing.start)
            throw new IllegalArgumentException("this.end()("+this.end()+") != directlyFollowing.start("+directlyFollowing.start+")");
        if(delegate != directlyFollowing.delegate)
            throw new IllegalArgumentException("delegate != directlyFollowing.delegate");
        return new SubStringStorage((StringStorage) delegate, this.start, directlyFollowing.end);
    }

    @Override public SubStringStorage delete(long start, long end) throws StorageSystemException {
        super.delete(start, end);
        return this;
    }
    @Override public SubStringStorage set(long start, String part, int off, int len) throws StorageSystemException {
        super.set(start, part, off, len);
        return this;
    }
    @Override public SubStringStorage set(long start, String part, int off) throws StorageSystemException {
        super.set(start, part, off);
        return this;
    }
    @Override public SubStringStorage set(long start, String part) throws StorageSystemException {
        return set(start, part, 0);
    }
    @Override public SubStringStorage append(String val) throws StorageSystemException {
        return set(contentSize(), val);
    }
}
