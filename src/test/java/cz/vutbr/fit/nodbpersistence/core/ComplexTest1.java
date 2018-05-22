package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test1ClassPrimitives;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComplexTest1 extends TestClassNoDbPersistence {

    @Test
    public void test1PersistClassPrimitives() {
        Test1ClassPrimitives class1 = new Test1ClassPrimitives();
        class1.setaBoolean(true);
        class1.setaBooleanWrap(true);

        class1.setaByte((byte) 9);
        class1.setaByteWrap((byte) 9);

        class1.setaShort((short) 10);
        class1.setaShortWrap((short) 10);

        class1.setAnInt(11);
        class1.setAnIntWrap(11);

        class1.setaLong(100L);
        class1.setaLongWrap(100L);

        class1.setaFloat(10.999f);
        class1.setaFloatWrap(10.999f);

        class1.setaDouble(10e10);
        class1.setaDoubleWrap(10e10);

        class1.setaString("hello world");
        objectId = pm.persist(class1);
        pm.flush();


        assertNotNull(objectId);
    }

    @Test
    public void test2ClassBoolean() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertTrue(test1loaded.isaBoolean());
        assertTrue(test1loaded.getaBooleanWrap());
    }

    @Test
    public void test3ClassByte() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(test1loaded.getaByte(), (byte) 9);
        assertEquals(test1loaded.getaByteWrap(), Byte.valueOf((byte) 9));
    }

    @Test
    public void test4ClassShort() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(test1loaded.getaShort(), (short) 10);
        assertEquals(test1loaded.getaShortWrap(), Short.valueOf((short) 10));
    }

    @Test
    public void test5ClassInteger() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(11, test1loaded.getAnInt());
        assertEquals(Integer.valueOf(11), test1loaded.getAnIntWrap());
    }

    @Test
    public void test6ClassLong() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(100L, test1loaded.getaLong());
        assertEquals(Long.valueOf(100L), test1loaded.getaLongWrap());
    }

    @Test
    public void test7ClassFloat() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(10.999f, test1loaded.getaFloat(), 0.000001d);
        assertEquals(Float.valueOf(10.999f), test1loaded.getaFloatWrap());
    }

    @Test
    public void test8ClassDouble() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(10e10, test1loaded.getaDouble(), 0.0001d);
        assertEquals(Double.valueOf(10e10), test1loaded.getaDoubleWrap());
    }

    @Test
    public void test9ClassString() {
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals("hello world", test1loaded.getaString());
    }


}
