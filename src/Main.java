
import cz.fit.persistence.core.PersistenceManagerImpl;

public class Main {

    private static final String DIR_PATH = "D:\\Documents\\FIT\\3BIT\\IBP\\test_dir";
    /*aasdfasdf*/

    public static void main(String[] args) {

        Test1 test = new Test1();
        test.setNumber(1);
        test.setText("hello");

        PersistenceManagerImpl pm = new PersistenceManagerImpl(DIR_PATH,null);
        try{
            pm.persist(test);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("hello world");
        }




        /*System.out.println(test.getText());

       List<Method> methods = new ArrayList<>();
       Class<?> klass = Test1.class;
       while (klass != Object.class) {
           final List<Method> allMethods = new ArrayList<>();
           for (final Method method:allMethods) {
               Annotation annotInstance = method.getAnnotation(TestAnnotation.class);

           }
       }
       */
    }
}
