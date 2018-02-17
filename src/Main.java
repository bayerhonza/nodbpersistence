
import cz.fit.persistence.core.PersistenceManagerImpl;
import cz.fit.persistence.core.Test1;

public class Main {

    public static void main(String[] args) {
        Test1 test = null;

        PersistenceManagerImpl pm = new PersistenceManagerImpl("/",null);
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
