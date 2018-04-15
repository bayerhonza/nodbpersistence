import cz.fit.persistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Test1 {

    @ObjectId
    private int objectId;

    private String text;

    private int number;

    private Test1 test1;


    private List<List<String>> list = new ArrayList<>();

    public int getNumber() {
        return number;
    }

    public Test1() {

    }


    public List<List<String>> getList() {
        return list;
    }

    public void setList(List<List<String>> list) {
        this.list = list;
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
