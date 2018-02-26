import cz.fit.persistence.annotations.ID;
import cz.fit.persistence.annotations.TestAnnotation;

public class Test1 {

    @ID
    Integer persistenceID;

    private String text;

    private int number;

    public Test1() {
        System.out.println("tvorim");
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setText(String text) {

        this.text = text;
    }

    @TestAnnotation(value = "test")
    public String getText() {
        return text;

    }
}
