import cz.fit.persistence.annotations.ObjectId;


public class Test1 {

    @ObjectId
    private int objectId;

    private String text;

    private int number;

    public int getNumber() {
        return number;
    }

    public Test1 setNumber(int number) {
        this.number = number;
        return this;
    }

    public Test1 setText(String text) {
        this.text = text;
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
