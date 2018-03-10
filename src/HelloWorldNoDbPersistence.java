
import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.PersistenceManagerFactory;
import cz.fit.persistence.core.PersistenceManagerFactoryBuilder;
import cz.fit.persistence.core.PersistenceSettings;

public class HelloWorldNoDbPersistence {


    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        PersistenceSettings persistenceContextSetting = new PersistenceSettings();

        persistenceContextSetting
                .setRootPath("D:\\Documents\\FIT\\3BIT\\IBP\\persisted_objects")
                .setCache(100);

        PersistenceManagerFactoryBuilder builder = new PersistenceManagerFactoryBuilder(persistenceContextSetting);
        PersistenceManagerFactory persistenceManagerFactory = builder.buildPersistenceManagerFactory();

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        Test1 test = new Test1();
        test.setNumber(1000)
                .setText("hello world");

        pm.persist(test);
    }
}
