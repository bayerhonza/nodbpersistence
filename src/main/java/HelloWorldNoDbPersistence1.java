import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactory;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactoryBuilder;
import cz.vutbr.fit.nodbpersistence.core.PersistenceSettings;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class HelloWorldNoDbPersistence1 {

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

        List<Integer[]> ahoj = new ArrayList<>();
        Integer[] arrayInt = {10,100,1000};
        ahoj.add(arrayInt);

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();



        HashMap<String,Integer> newMap = new HashMap<>();
        newMap.put("ahoj",1);

        Test3 test3 = new Test3("adsfas");
        test3.strings[0] = new ArrayList<>();
        test3.strings[0].add("sdfasdf");
        test3.strings[1] = null;
        test3.strings[2] = ahoj;
        test3.enume = TestEnum.A;
        test3.map = newMap;
        test3.map.put("bbbb",1);
        Test3 test31 = new Test3("bbbbbb");
        test31.map = newMap;
        test3.map.put("cccc",2);



        pm.persist(test3);
        pm.persist(test31);
        pm.flush();


        Test3 test_loaded = pm.load(1L, Test3.class);
        Test3 test_loaded2 = pm.load(2L, Test3.class);

        System.out.println(test_loaded.map);
        System.out.println(test_loaded2.map);
        System.out.println(test_loaded.toString());
    }
}
