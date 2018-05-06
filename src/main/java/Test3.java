import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.util.*;

public class Test3 extends Test2 {

    @ObjectId
    public Long objectId;

    public List[] strings = new List[3];

    public Map<Object,Object> map = new HashMap<>();

    public TestEnum enume;

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
