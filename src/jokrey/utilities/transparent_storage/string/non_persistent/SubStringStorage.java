package jokrey.utilities.transparent_storage.string.non_persistent;

import jokrey.utilities.transparent_storage.SubStorage;
import jokrey.utilities.transparent_storage.TransparentStorage;

public class SubStringStorage extends SubStorage<String> {
    public SubStringStorage(TransparentStorage<String> delegate, long end, long start) {
        super(delegate, start, end);
    }

    public static SubStringStorage empty() {
        return new SubStringStorage(new StringStorageSystem(0), 0, 0);
    }
}
