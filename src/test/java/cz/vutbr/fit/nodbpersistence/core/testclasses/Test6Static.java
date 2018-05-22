package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

public class Test6Static {

    public static Test6Class1 class1 = new Test6Class1();

    @ObjectId
    public Long objectId;

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
