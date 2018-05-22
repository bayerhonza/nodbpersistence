package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test3Enum1;
import cz.vutbr.fit.nodbpersistence.core.testclasses.Test3SimpleTypes;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComplexTest3 extends TestClassNoDbPersistence {

    @Test
    public void test1PersistClass1() throws URISyntaxException {
        Test3SimpleTypes test3 = new Test3SimpleTypes();

        test3.setEnum1(Test3Enum1.B);

        test3.setCharSequence("char sequence");

        test3.setNumber(100);

        test3.setDate(new Date(100000L));

        test3.setUri(new URI("www.google.com"));

        test3.setLocale(new Locale("en"));

        objectId = pm.persist(test3);
        pm.flush();


        assertNotNull(objectId);


    }

    @Test
    public void test2Enum() {
        Test3SimpleTypes test3Loaded = pm.load(objectId, Test3SimpleTypes.class);
        assertEquals(Test3Enum1.B, test3Loaded.getEnum1());
    }

    @Test
    public void test3CharSequence() {
        Test3SimpleTypes test3Loaded = pm.load(objectId, Test3SimpleTypes.class);
        assertEquals("char sequence", test3Loaded.getCharSequence());
    }

    @Test
    public void test4Number() {
        Test3SimpleTypes test3Loaded = pm.load(objectId, Test3SimpleTypes.class);
        assertEquals(100, test3Loaded.getNumber());
    }

    @Test
    public void test5Date() {
        Test3SimpleTypes test3Loaded = pm.load(objectId, Test3SimpleTypes.class);
        assertEquals(new Date(100000L), test3Loaded.getDate());
    }

    @Test
    public void test6URI() {
        Test3SimpleTypes test3Loaded = pm.load(objectId, Test3SimpleTypes.class);
        assertEquals(URI.create("www.google.com"), test3Loaded.getUri());
    }

    @Test
    public void test7Locale() {
        Test3SimpleTypes test3Loaded = pm.load(objectId, Test3SimpleTypes.class);
        assertEquals(new Locale("en"), test3Loaded.getLocale());
    }
}
