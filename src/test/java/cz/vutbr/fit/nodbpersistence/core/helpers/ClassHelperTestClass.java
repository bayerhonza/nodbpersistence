package cz.vutbr.fit.nodbpersistence.core.helpers;

public class ClassHelperTestClass {

    public static String testStatic = "testStatic";
    public static final Long testFinalStatic = 1L;


    private Long number;
    public String string;
    protected Integer integer;

    private final String finalString;

    public ClassHelperTestClass() {
        this.finalString = "testing";
        this.number = 100L;
        this.string = "hello";
        this.integer = 100;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
