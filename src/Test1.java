import cz.fit.persistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Test1 {

    @ObjectId
    private int objectId;

    private String text;

    private int number;

    private Test2 test2;

    private List<List<Test2>> list1 = new ArrayList<>();

    public int getNumber() {
        return number;
    }

    public Test1() {

    }

    public Test1 setNumber(int number) {
        this.number = number;
        return this;
    }

    public List<List<Test2>> getList() {
        return list1;
    }

    public Test1 setText(String text) {
        this.text = text;
        return this;
    }

    public Test2 getTest2() {
        return test2;
    }

    public Test1 setTest2(Test2 test2) {
        this.test2 = test2;
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
