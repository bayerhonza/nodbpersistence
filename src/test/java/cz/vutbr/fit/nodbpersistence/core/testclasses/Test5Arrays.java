package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

public class Test5Arrays {

    @ObjectId
    public Long objectId;

    private Integer[] integers;
    private String[][] string;

    public Integer[] getIntegers() {
        return integers;
    }

    public void setIntegers(Integer[] integers) {
        this.integers = integers;
    }

    public String[][] getString() {
        return string;
    }

    public void setString(String[][] string) {
        this.string = string;
    }
}
