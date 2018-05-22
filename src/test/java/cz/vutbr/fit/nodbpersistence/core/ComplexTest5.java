package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test5Arrays;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComplexTest5 extends TestClassNoDbPersistence {


    @Test
    public void test1PersistArrays() {
        Test5Arrays t5Arrays = new Test5Arrays();
        Integer[] integers = new Integer[]{1, 2, 3};
        t5Arrays.setIntegers(integers);
        String[][] strings = new String[][]{
                {"a", "b", "c"},
                {"d", "e", null}
        };
        t5Arrays.setString(strings);
        objectId = pm.persist(t5Arrays);
        pm.flush();

        assertNotNull(objectId);
    }

    @Test
    public void test2LoadIntegerArray() {
        Test5Arrays test5ArraysLoaded = pm.load(objectId, Test5Arrays.class);
        assertEquals(Integer.valueOf(1), test5ArraysLoaded.getIntegers()[0]);
        assertEquals(Integer.valueOf(2), test5ArraysLoaded.getIntegers()[1]);
        assertEquals(Integer.valueOf(3), test5ArraysLoaded.getIntegers()[2]);

    }

    @Test
    public void test3LoadStringArray() {
        Test5Arrays test5ArraysLoaded = pm.load(objectId, Test5Arrays.class);
        assertEquals("a", test5ArraysLoaded.getString()[0][0]);
        assertEquals("d", test5ArraysLoaded.getString()[1][0]);
        assertNull(test5ArraysLoaded.getString()[1][2]);
    }
}
