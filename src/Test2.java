import cz.fit.persistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.List;

class Test2 {

    @ObjectId
    private Integer objectId;
    private List<Test1> list = new ArrayList<>();



    private String text;

    public Test2() {
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
