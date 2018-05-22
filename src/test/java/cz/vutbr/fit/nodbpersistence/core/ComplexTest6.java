package cz.vutbr.fit.nodbpersistence.core;


import cz.vutbr.fit.nodbpersistence.core.testclasses.Test6Class1;
import cz.vutbr.fit.nodbpersistence.core.testclasses.Test6Static;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComplexTest6 extends TestClassNoDbPersistence {

    @Test
    public void test1PersistStatic() {
        Test6Static.class1.setString("static object");
        Test6Static test6Class1 = new Test6Static();
        test6Class1.setString("hello");
        objectId = pm.persist(test6Class1);
        pm.flush();


        assertNotNull(objectId);
    }

    @Test
    public void test2LoadStatic() {
        Test6Static.class1 = new Test6Class1();
        Test6Static.class1.setString("changed static object");
        Test6Class1 test6Class1Loaded = pm.load(objectId, Test6Class1.class);
        Assert.assertEquals("static object", Test6Static.class1.getString());

    }
}
