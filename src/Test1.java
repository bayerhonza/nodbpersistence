import cz.fit.persistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Test1 {

    @ObjectId
    private int objectId;

    private String text;

    private int number;

    private Test1 test1;


    private Set<Test2> set = new HashSet<>();

    public int getNumber() {
        return number;
    }

    public Test1() {

    }


    public Set<Test2> getSet() {
        return set;
    }

    public void setSet(Set<Test2> set) {
        this.set = set;
    }

    public Test1 setNumber(int number) {
        this.number = number;
        return this;
    }

    public Test1 setText(String text) {
        this.text = text;
        return this;
    }

    public Test1 getTest1() {
        return test1;
    }

    public Test1 setTest1(Test1 test2) {
        this.test1 = test2;
        return this;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "objectId: " + objectId + ", text: " + text;
    }
}
