import cz.fit.persistence.annotations.ID;
import cz.fit.persistence.annotations.TestAnnotation;

public class Test1 {

    @ID
    Integer persistenceID;

    private String text;

    private int number;

    Test1() {
        System.out.println("tvorim");
    }

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

    @TestAnnotation(value = "test")
    public String getText() {
        return text;

    }
}
