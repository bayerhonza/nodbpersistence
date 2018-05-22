package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

public class Test7Class1 {
    @ObjectId
    public Long objectId;

    private String string;

    public String getString() {
        return string;
    }

    public Test7Class1 setString(String string) {
        this.string = string;
        return this;
    }
}
