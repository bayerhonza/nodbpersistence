package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test2NoObjectId;
import cz.vutbr.fit.nodbpersistence.exceptions.NoObjectIdFoundException;
import org.junit.Test;

public class ComplexTest2 extends TestClassNoDbPersistence {

    @Test(expected = NoObjectIdFoundException.class)
    public void testNoObjectId() {
        Test2NoObjectId test2NoObjectId = new Test2NoObjectId();
        pm.persist(test2NoObjectId);
    }

}
