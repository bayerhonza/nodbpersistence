package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test1ClassPrimitives;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComplexTest1 extends TestClassNoDbPersistence {

    @BeforeClass
    public static void beforePersistClassPrimitives() {
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
        PersistenceManager pm = testRule.getPersistenceManager();
        objectId = pm.persist(class1);
        pm.flush();

    }

    @Test
    public void testClassBoolean() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertTrue(test1loaded.isaBoolean());
        assertTrue(test1loaded.getaBooleanWrap());
    }

    @Test
    public void testClassByte() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(test1loaded.getaByte(), (byte) 9);
        assertEquals(test1loaded.getaByteWrap(), Byte.valueOf((byte) 9));
    }

    @Test
    public void testClassShort() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(test1loaded.getaShort(), (short) 10);
        assertEquals(test1loaded.getaShortWrap(), Short.valueOf((short) 10));
    }

    @Test
    public void testClassInteger() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(11, test1loaded.getAnInt());
        assertEquals(Integer.valueOf(11), test1loaded.getAnIntWrap());
    }

    @Test
    public void testClassLong() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(100L, test1loaded.getaLong());
        assertEquals(Long.valueOf(100L), test1loaded.getaLongWrap());
    }

    @Test
    public void testClassFloat() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(10.999f, test1loaded.getaFloat(), 0.000001d);
        assertEquals(Float.valueOf(10.999f), test1loaded.getaFloatWrap());
    }

    @Test
    public void testClassDouble() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals(10e10, test1loaded.getaDouble(), 0.0001d);
        assertEquals(Double.valueOf(10e10), test1loaded.getaDoubleWrap());
    }

    @Test
    public void testClassString() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test1ClassPrimitives test1loaded = pm.load(objectId, Test1ClassPrimitives.class);
        assertEquals("hello world", test1loaded.getaString());
    }


}
