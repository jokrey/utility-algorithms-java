package jokrey.utilities.transparent_storage;

import java.util.ArrayList;
import java.util.Objects;

import static jokrey.utilities.transparent_storage.ObservableTransparentStorage.Change.*;

/**
 * Offers a view into a specific part of the given, underlying storage.
 *
 * All methods only function within the boundaries set upfront.
 * This method will never leave the underlying storage smaller, only (potentially) larger. But never larger than start+end
 *    Deletion is not possible because that would change other parts of the delegated storage as well, use "set" to null values.
 * @author jokrey
 */
public class ObservableTransparentStorage<SF> implements TransparentStorage<SF> {
    protected TransparentStorage<SF> delegate;
    /**
     * @param delegate delegated storage
     */
    public ObservableTransparentStorage(TransparentStorage<SF> delegate) {
        this.delegate = delegate;
        if(delegate==null)
            throw new NullPointerException("delegate==null");
    }

    private final ArrayList<DataChangedListener> changeListeners = new ArrayList<>();
    enum Change {SET, REMOVE}
    interface DataChangedListener { void changed(long from, long to, Change change);}
    public void addDataChangedListener(DataChangedListener changed) { changeListeners.add(changed); }
    public void removeDataChangedListener(DataChangedListener changed) { changeListeners.remove(changed); }
    private void fireDataChanged(long from, long to, Change change) {
        for (DataChangedListener l : changeListeners)
            l.changed(from, to, change);
    }

    @Override public int len(SF part) {
        return delegate.len(part);
    }
    @Override public SF sub(long start, long end) {
        return delegate.sub(start, end);
    }
    @Override public ObservableTransparentStorage<SF> set(long start, SF part, int off, int len) throws StorageSystemException {
        delegate.set(start, part, off, len);
        fireDataChanged(start, start+len(part), SET);
        return this;
    }

    @Override public long contentSize() {
        return delegate.contentSize();
    }
    @Override public boolean isEmpty() { return delegate.isEmpty(); }
    @Override public void setContent(SF content) { delegate.setContent(content); }
    @Override public SF getContent() {
        return delegate.getContent();
    }

    @Override public void clear() {
        long previousContentSize = contentSize();
        delegate.clear();
        fireDataChanged(0, previousContentSize, REMOVE);
    }
    @Override public TransparentStorage<SF> delete(long start, long end) throws StorageSystemException {
        delegate.delete(start, end);
        fireDataChanged(start, end, REMOVE);
        return this;
    }
    /**
     * Closes underlying storage
     */
    @Override public void close() throws Exception {
        delegate.close();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservableTransparentStorage<?> that = (ObservableTransparentStorage<?>) o;
        return delegate == that.delegate;
    }
    @Override public int hashCode() {
        return Objects.hash(System.identityHashCode(delegate));
    }

    @Override public String toString() {
        return "DelegatedStorage<SF>{" +
                ", delegate.size=" + delegate.contentSize() +
                '}';
    }
}
