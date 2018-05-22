package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test7Class1;
import cz.vutbr.fit.nodbpersistence.core.testclasses.Test7Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComplexTest7 extends TestClassNoDbPersistence {

    @Test
    public void test1PersistMaps() {
        Test7Maps test7Maps = new Test7Maps();
        HashMap<Integer, String> integerStringHashMap = new HashMap<>();
        integerStringHashMap.put(1, "one");
        integerStringHashMap.put(2, "two");
        test7Maps.setIntegerStringHashMap(integerStringHashMap);

        Map<Long, Map<String, Test7Class1>> map = new IdentityHashMap<>();
        ConcurrentHashMap<String, Test7Class1> concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put("testing", new Test7Class1().setString("testing"));
        map.put(10L, concurrentMap);
        test7Maps.setMapLongMap(map);
        objectId = pm.persist(test7Maps);
        pm.flush();

        Assert.assertNotNull(objectId);
    }

    @Test
    public void test2LoadStringMap() {
        Test7Maps test6Class1Loaded = pm.load(objectId, Test7Maps.class);
        assertEquals(2, test6Class1Loaded.getIntegerStringHashMap().size());
        assertEquals("one", test6Class1Loaded.getIntegerStringHashMap().get(1));
        assertEquals("two", test6Class1Loaded.getIntegerStringHashMap().get(2));
    }

    @Test
    public void test2LoadMapLong() {
        Test7Maps test6Class1Loaded = pm.load(objectId, Test7Maps.class);
        assertTrue(test6Class1Loaded.getMapLongMap().containsKey(10L));
        assertTrue(test6Class1Loaded.getMapLongMap().get(10L).containsKey("testing"));
        assertEquals("testing", test6Class1Loaded.getMapLongMap().get(10L).get("testing").getString());
    }
}
