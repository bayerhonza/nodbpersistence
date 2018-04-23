import cz.fit.persistence.annotations.ObjectId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test3 extends Test2 {

    @ObjectId
    public Long objectId;

    public Set<Test1> listTest1 = new HashSet<>();

    public Test3(String string) {
        super(string);
    }


    public Set<Test1> getListTest1() {
        return listTest1;
    }

    public void setListTest1(Set<Test1> listTest1) {
        this.listTest1 = listTest1;
    }




}
