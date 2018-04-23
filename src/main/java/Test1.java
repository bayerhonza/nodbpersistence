import cz.fit.persistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Test1 {

    @ObjectId
    public Long objectId;

    private String text;

    private int number;

    private Test1 test1;


    private Set<Test2> set = new HashSet<>();

    public int getNumber() {
        return number;
    }

    public Test1(String text) {
        this.text = text;

    }


    public Set<Test2> getSet() {
        return set;
    }

    public void setSet(Set<Test2> set) {
        this.set = set;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Test1 getTest1() {
        return test1;
    }

    public void setTest1(Test1 test2) {
        this.test1 = test2;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "objectId: " + objectId + ", text: " + text;
    }
}
