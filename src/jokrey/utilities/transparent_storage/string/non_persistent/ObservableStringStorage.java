package jokrey.utilities.transparent_storage.string.non_persistent;

import jokrey.utilities.transparent_storage.ObservableTransparentStorage;

/**
 * Offers a view into a specific part of the given, underlying storage.
 *
 * All methods only function within the boundaries set upfront.
 * This method will never leave the underlying storage smaller, only (potentially) larger. But never larger than start+end
 *    Deletion is not possible because that would change other parts of the delegated storage as well, use "set" to null values.
 * @author jokrey
 */
public class ObservableStringStorage extends ObservableTransparentStorage<String> implements StringStorage {
    /**
     * @param delegate delegated storage
     */
    public ObservableStringStorage(StringStorage delegate) {
        super(delegate);
    }

    @Override public long indexOf(String s, long from) {
        return ((StringStorage) delegate).indexOf(s, from);
    }
    @Override public long lastIndexOf(String s, long backwardsFrom) {
        return ((StringStorage) delegate).lastIndexOf(s, backwardsFrom);
    }
}
