import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.PersistenceManagerFactory;
import cz.fit.persistence.core.PersistenceManagerFactoryBuilder;
import cz.fit.persistence.core.PersistenceSettings;
import cz.fit.persistence.exceptions.PersistenceCoreException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Test1 test = new Test1();
        test.setNumber(1)
                .setText("hello");

        List<List<String >> list = test.getList();
        List<String> subList = new ArrayList<>();
        subList.add("afsdfasdf");
        subList.add("ahoj");
        subList.add("ccccc");
        list.add(subList);



        Test1 test12 = new Test1()
                .setNumber(32165151)
                .setText("jin")
                .setTest1(test);

        /*Test2 test2 = new Test2();
        test2.addToList("ahoj");

        Test2 test21 = new Test2();

        List<List<Test2>> list = test.getList();
        List<Test2> nextList = new ArrayList<>();
        nextList.add(test2);
        nextList.add(test21);
        list.add(nextList);*/



        /*Test2 test2 = new Test2();
        test2.addToList("ahoj");
        Test1 test12 = new Test1()
                .setNumber(2)
                .setText("asdfasdfasdf")
                .setTest2(test2);
        */
        pm.persist(test12);

        Test1 test1111 = pm.load(1,Test1.class);
        System.out.println(test1111);

        /*Test1 test_loaded = (Test1) pm.load(1, Test1.class);
        System.out.println(test_loaded.toString());*/
    }
}
