import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactory;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactoryBuilder;
import cz.vutbr.fit.nodbpersistence.core.PersistenceSettings;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

class HelloWorldNoDbPersistence3 {

    public static final Integer count = 1;

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        PersistenceSettings persistenceContextSetting = new PersistenceSettings();
        persistenceContextSetting.loadFromPropertiesFile();
        PersistenceManagerFactory persistenceManagerFactory;
        try {
            PersistenceManagerFactoryBuilder builder = new PersistenceManagerFactoryBuilder(persistenceContextSetting);
            persistenceManagerFactory = builder.buildPersistenceManagerFactory();
        } catch (PersistenceCoreException ex) {
            ex.printStackTrace();
            return;
        }

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        for (int i = 0; i < 100; i++) {
            Test3 test3 = new Test3(String.valueOf(i));
            pm.persist(test3);
        }
        pm.flush();

        Test3 test3loaded = pm.load(999L, Test3.class);
        System.out.println(test3loaded);
    }
}
