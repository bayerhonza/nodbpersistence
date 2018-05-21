package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.util.*;

public class Test1Class3 extends Test1Class2 {

    @ObjectId
    public Long objectId;

    public static HashMap<Integer, String> testStatic = new HashMap<>();

    public List[] lists = new List[3];

    public Map<String, Integer> map = new HashMap<>();

    public Test1Enum1 enume;

    public Set<Test1ClassPrimitives> test1Class1s = new HashSet<>();

    public Test1Class3(String string) {
        super(string);
    }

    public Test1Class3() {
        super();
    }


    public Set<Test1ClassPrimitives> getTest1Class1s() {
        return test1Class1s;
    }

    public void setTest1Class1s(Set<Test1ClassPrimitives> test1Class1s) {
        this.test1Class1s = test1Class1s;
    }


}
