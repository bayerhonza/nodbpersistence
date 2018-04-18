import cz.fit.persistence.annotations.ObjectId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test3 extends Test1 {

    @ObjectId
    public String objectId;

    public Test3() {
        super("asdfasdf");
    }

    private Set<Test1> listTest1 = new HashSet<>();



}
