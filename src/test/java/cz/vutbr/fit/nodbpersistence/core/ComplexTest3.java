package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test3;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class ComplexTest3 extends TestClassNoDbPersistence {

    @BeforeClass
    public static void testPersistClass1() throws URISyntaxException {
        Test3 test3 = new Test3();
        test3.setCharSequence("char sequence");

        test3.setNumber(100);

        test3.setDate(new Date());

        test3.setUri(new URI("www.google.com"));

        test3.setLocale(new Locale("en"));

        PersistenceManager pm = testRule.getPersistenceManager();
        objectId = pm.persist(test3);
        pm.flush();


    }

    @Test
    public void testCharSequence() {
        PersistenceManager pm = testRule.getPersistenceManager();
        Test3 test3Loaded = pm.load(objectId, Test3.class);

        assertEquals("char sequence", test3Loaded.getCharSequence());
    }
}
