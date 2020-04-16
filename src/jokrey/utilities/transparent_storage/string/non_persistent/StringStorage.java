package jokrey.utilities.transparent_storage.string.non_persistent;

import jokrey.utilities.transparent_storage.TransparentStorage;

import java.util.ArrayList;

/**
 * @author jokrey
 */
public interface StringStorage extends TransparentStorage<String> {
    /**@see String#indexOf(String) */
    default long indexOf(String s) {
        return indexOf(s, 0);
    }

    /**@see String#contains(CharSequence) */
    default boolean contains(String s) {
        return indexOf(s) > -1;
    }

    /**@see String#indexOf(String, int)
     * @return */
    long indexOf(String s, long from);
    /**@see String#lastIndexOf(String, int)
     * @return */
    long lastIndexOf(String s, long backwardsFrom);
    /**@see String#split(String) */
    default SubStringStorage[] split(String s) {
        ArrayList<SubStringStorage> splits = new ArrayList<>();
        long index = 0;
        while(true) {
            long newIndex = indexOf(s, index);
            if(newIndex == -1) {
                splits.add(subStorage(index));
                break;
            }
            splits.add(subStorage(index, newIndex));
            index = newIndex + s.length();
        }
        return splits.toArray(new SubStringStorage[0]);
    }




    //OVERRIDE TO CORRECT RETURN TYPE
    default SubStringStorage subStorage(long start) {
        return new SubStringStorage(this, start, contentSize());
    }
    default SubStringStorage subStorage(long start, long end) {
        return new SubStringStorage(this, start, end);
    }
    default SubStringStorage[] split(long at) {
        return new SubStringStorage[] {
                new SubStringStorage(this, 0, at),
                new SubStringStorage(this, at, contentSize())
        };
    }
}
