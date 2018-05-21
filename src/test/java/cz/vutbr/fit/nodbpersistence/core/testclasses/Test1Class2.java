package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.List;

class Test1Class2 extends Test1ClassPrimitives {

    @ObjectId
    private long objectId;
    private List<Test1ClassPrimitives> list = new ArrayList<>();

    private String text;

    public Test1Class2(String string) {
        //super(string);
        list.add(this);
    }

    public Test1Class2() {
        super();
    }

    public void setList(List<Test1ClassPrimitives> list) {
        this.list = list;
    }

    public List<Test1ClassPrimitives> getList() {
        return list;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
