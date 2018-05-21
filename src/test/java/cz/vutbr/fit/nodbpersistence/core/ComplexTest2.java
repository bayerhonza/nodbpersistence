package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test2;
import cz.vutbr.fit.nodbpersistence.exceptions.NoObjectIdFoundException;
import org.junit.Rule;
import org.junit.Test;

public class ComplexTest2 {

    @Rule
    public NoDbRule testRule = new NoDbRule();

    @Test(expected = NoObjectIdFoundException.class)
    public void testNoObjectId() {
        Test2 test2 = new Test2();
        PersistenceManager pm = testRule.getPersistenceManager();
        pm.persist(test2);
    }
}
