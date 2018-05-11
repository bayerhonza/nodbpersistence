import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.List;

class Test2 extends Test1 {

    @ObjectId
    private long objectId;
    private List<Test1> list = new ArrayList<>();

    private String text;

    public Test2(String string) {
        super(string);
        list.add(this);
    }

    public Test2() {
        super();
    }

    public void setList(List<Test1> list) {
        this.list = list;
    }

    public List<Test1> getList() {
        return list;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
