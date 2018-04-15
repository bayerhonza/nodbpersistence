import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.PersistenceManagerFactory;
import cz.fit.persistence.core.PersistenceManagerFactoryBuilder;
import cz.fit.persistence.core.PersistenceSettings;
import cz.fit.persistence.exceptions.PersistenceCoreException;

class HelloWorldNoDbPersistence {


    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        PersistenceSettings persistenceContextSetting = new PersistenceSettings();

        persistenceContextSetting
                .setRootPath("D:\\Documents\\FIT\\3BIT\\IBP\\persisted_objects")
                .setCache(100);
        PersistenceManagerFactory persistenceManagerFactory;
        try {
            PersistenceManagerFactoryBuilder builder = new PersistenceManagerFactoryBuilder(persistenceContextSetting);
            persistenceManagerFactory = builder.buildPersistenceManagerFactory();
        } catch (PersistenceCoreException ex) {
            ex.printStackTrace();
            return;
        }

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        /*long elapsedTime = 0;
        for (int i = 0; i < 1000; i++) {
            Test1 test = new Test1()
                    .setNumber(i)
                    .setText("text: " + i);
            long before = System.nanoTime();
            pm.persist(test);
            elapsedTime += System.nanoTime() - before;
        }


        System.out.println(TimeUnit.NANOSECONDS.toMillis(elapsedTime));*/

        Test1 test = new Test1(null);
        test.setNumber(1)
                .setText("hello");

        Test2 test2 = new Test2();
        test2.addToList("ahoj");
        Test1 test12 = new Test1(test2)
                .setNumber(2)
                .setText("asdfasdfasdf");

        pm.persist(test);
        pm.persist(test12);

        pm.persist(test2);

        test.setText("aaaaa");
        pm.persist(test);
        System.out.println(test2.getList().toString());

        Test1 test_loaded = (Test1) pm.load(1, Test1.class);
        System.out.println(test_loaded.toString());
    }
}
