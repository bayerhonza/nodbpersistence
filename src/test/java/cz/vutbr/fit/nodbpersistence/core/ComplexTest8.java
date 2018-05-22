package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test8Class1;
import cz.vutbr.fit.nodbpersistence.core.testclasses.Test8Class2;
import cz.vutbr.fit.nodbpersistence.core.testclasses.Test8Extends;
import org.junit.Assert;
import org.junit.Test;

public class ComplexTest8 extends TestClassNoDbPersistence {

    @Test
    public void test1PersistExtended() {
        Test8Extends test8Extends = new Test8Extends();
        test8Extends.string = "test8extends";
        ((Test8Class2) test8Extends).string = "test8class2";
        ((Test8Class1) test8Extends).string = "test8class1";
        objectId = pm.persist(test8Extends);
        pm.flush();

        Assert.assertNotNull(objectId);
    }

    @Test
    public void test2LoadExtended() {
        Test8Extends test8ExtendsLoaded = pm.load(objectId, Test8Extends.class);
        Assert.assertEquals("test8extends", test8ExtendsLoaded.string);
        Assert.assertEquals("test8class2", ((Test8Class2) test8ExtendsLoaded).string);
        Assert.assertEquals("test8class1", ((Test8Class1) test8ExtendsLoaded).string);
    }
}
