package jokrey.utilities.simple.data_structure;

import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * todo - use composite keys instead
 *
 * @author jokrey
 */
public class BadConcurrentMultiKeyMap<K1, K2, V> {
    HashMap<K1, K2> map1to2 = new HashMap<>();
    HashMap<K2, K1> map2to1 = new HashMap<>();
    HashMap<K1, V> mapToV = new HashMap<>();

    public synchronized V put(K1 k1, K2 k2, V v) {
        V previous = mapToV.put(k1, v);
        map1to2.put(k1, k2);
        map2to1.put(k2, k1);
        return previous;
    }
    public synchronized V removeBy1(K1 k1) {
        K2 k2 = map1to2.remove(k1);
        map2to1.remove(k2);
        return mapToV.remove(k1);
    }
    public synchronized V removeBy2(K2 k2) {
        K1 k1 = map2to1.remove(k2);
        map1to2.remove(k1);
        return mapToV.remove(k1);
    }
    public synchronized V getBy1(K1 k1) {
        return mapToV.get(k1);
    }
    public synchronized V getBy2(K2 k2) {
        K1 k1 = map2to1.get(k2);
        if(k1 != null)
            return getBy1(k1);
        else
            return null;
    }

    public synchronized boolean containsBy1(K1 k1) {
        return map1to2.containsKey(k1);
    }
    public synchronized boolean containsBy2(K2 k2) {
        return map2to1.containsKey(k2);
    }
    public synchronized V[] values(V[] empty_va) {
        return mapToV.values().toArray(empty_va);
    }
    public synchronized int size() {
        return mapToV.size();
    }

    @Test public void demonstration() {
        BadConcurrentMultiKeyMap<Integer, String, String> map = new BadConcurrentMultiKeyMap<>();

        map.put(1, "1", "v1");
        map.put(2, "2", "v2");
        assertEquals("v1", map.getBy1(1));
        assertEquals("v1", map.getBy2("1"));
        assertEquals("v2", map.getBy1(2));
        assertEquals("v2", map.getBy2("2"));
        assertNull(map.getBy1(3));
        assertNull(map.getBy2("3"));

        map.removeBy1(1);
        assertNull(map.getBy1(1));
        assertNull(map.getBy2("1"));

        map.removeBy2("2");
        assertNull(map.getBy1(2));
        assertNull(map.getBy2("2"));
    }
}
