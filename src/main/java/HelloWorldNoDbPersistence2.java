import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactory;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactoryBuilder;
import cz.vutbr.fit.nodbpersistence.core.PersistenceSettings;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

class HelloWorldNoDbPersistence2 {

    public static final Integer count = 1;

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        PersistenceSettings persistenceContextSetting = new PersistenceSettings()
                .setRootPath("D:\\Documents\\FIT\\3BIT\\IBP\\persisted_objects");
        PersistenceManagerFactory persistenceManagerFactory;
        try {
            PersistenceManagerFactoryBuilder builder = new PersistenceManagerFactoryBuilder(persistenceContextSetting);
            persistenceManagerFactory = builder.buildPersistenceManagerFactory();
        } catch (PersistenceCoreException ex) {
            ex.printStackTrace();
            return;
        }

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        Test3 test3 = new Test3("aaaaaa");
        pm.persist(test3);

        Test3 test_loaded = pm.load(1L, Test3.class);
        pm.persist(test_loaded);
        System.out.println(test_loaded.toString());
    }
}
