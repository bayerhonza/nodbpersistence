package cz.vutbr.fit.nodbpersistence.core;

import org.junit.ClassRule;

public class TestClassNoDbPersistence {

    public static Long objectId;

    @ClassRule
    public static NoDbRule testRule = new NoDbRule();
}
