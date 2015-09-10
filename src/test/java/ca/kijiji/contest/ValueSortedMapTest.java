package ca.kijiji.contest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Ignore
public class ValueSortedMapTest {

    Map<String, Integer> initialMap;

    @Before
    public void init() {
        initialMap = new HashMap<String, Integer>();
        initialMap.put("A", 5);
        initialMap.put("B", 4);
        initialMap.put("C", 2);
        initialMap.put("D", 1);
        initialMap.put("E", 3);
    }


    @Test
    public void testValueOrderFirstKey() {
        SortedMap<String, Integer> sortedResult = new ValueSortedMap<String, Integer>(new MapValueComparator<String, Integer>());
        sortedResult.putAll(initialMap);

        assertEquals(sortedResult.firstKey(), "A");
        assertEquals(sortedResult.get(sortedResult.firstKey()), Integer.valueOf(5));
    }

    @Test
    public void testValueOrderLastKey() {
        SortedMap<String, Integer> sortedResult = new ValueSortedMap<String, Integer>(new MapValueComparator<String, Integer>());
        sortedResult.putAll(initialMap);

        assertEquals(sortedResult.lastKey(), "D");
        assertEquals(sortedResult.get(sortedResult.lastKey()), Integer.valueOf(1));
    }

    @Test
    public void testValueOrderInitialMapUnchanged() {
        SortedMap<String, Integer> sortedResult = new ValueSortedMap<String, Integer>(new MapValueComparator<String, Integer>());
        sortedResult.putAll(initialMap);

        sortedResult.put("F", 10);

        assertFalse(initialMap.containsKey("F"));
        assertEquals(sortedResult.firstKey(), "F");
        assertEquals(sortedResult.get(sortedResult.firstKey()), Integer.valueOf(10));
    }

    @Test
    public void testValueOrderKeyOrderOnSameValues() {
        SortedMap<String, Integer> sortedResult = new ValueSortedMap<String, Integer>(new MapValueComparator<String, Integer>());
        sortedResult.putAll(initialMap);

        sortedResult.put("G", 6);
        assertEquals("G", sortedResult.firstKey());

        sortedResult.put("H", 6);
        assertEquals("G", sortedResult.firstKey());

        sortedResult.put("F", 6);
        assertEquals("F", sortedResult.firstKey());
    }

    @Test
    public void testValueOrderRemove() {
        SortedMap<String, Integer> sortedResult = new ValueSortedMap<String, Integer>(new MapValueComparator<String, Integer>());
        sortedResult.putAll(initialMap);

        sortedResult.remove("A");

        assertEquals(sortedResult.firstKey(), "B");
        assertEquals(sortedResult.get(sortedResult.firstKey()), Integer.valueOf(4));
    }
}
