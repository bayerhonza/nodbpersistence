import cz.fit.persistence.annotations.ObjectId;

import java.util.ArrayList;
import java.util.List;

class Test2 {

    @ObjectId
    private Integer objectId;
    private List<String> list = new ArrayList<>();

    public Test2() {
    }

    ;

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    public void addToList(String string) {
        list.add(string);
    }
}
