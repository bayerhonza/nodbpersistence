
import cz.fit.persistence.bootstrap.Bootstrap;
import cz.fit.persistence.core.PersistenceContext;
import cz.fit.persistence.core.PersistenceManagerImpl;

public class Main {

    private static final String DIR_PATH = "D:\\Documents\\FIT\\3BIT\\IBP\\test_dir";

    private PersistenceContext context;

    public static void main(String[] args) {
       System.out.println(System.getProperty("user.dir"));
       Bootstrap.initPersistence();
    }
}
