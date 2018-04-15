import cz.fit.persistence.annotations.ObjectId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test3 {

    @ObjectId
    private int objectId;

    private Set<Test1> listTest1 = new HashSet<>();

    public Set<Test1> getListTest1() {
        return listTest1;
    }

    public void setListTest1(Set<Test1> listTest1) {
        this.listTest1 = listTest1;
    }

}
