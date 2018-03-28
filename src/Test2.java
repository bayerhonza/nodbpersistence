import java.util.ArrayList;
import java.util.List;

class Test2 {
    private List<String> list = new ArrayList<>();

    public Test2() {
        list.add("ahoj");
    }

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
