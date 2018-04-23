import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.PersistenceManagerFactory;
import cz.fit.persistence.core.PersistenceManagerFactoryBuilder;
import cz.fit.persistence.core.PersistenceSettings;
import cz.fit.persistence.core.helpers.ClassHelper;
import cz.fit.persistence.exceptions.PersistenceCoreException;

import java.lang.reflect.Field;
import java.util.*;

class HelloWorldNoDbPersistence1 {

    public static final Integer count = 1;

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        PersistenceSettings persistenceContextSetting = new PersistenceSettings();

        persistenceContextSetting
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
        System.out.println(test_loaded.toString());
    }
}
