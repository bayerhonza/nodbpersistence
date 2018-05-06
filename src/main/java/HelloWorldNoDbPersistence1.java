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

        /*Test3 test3 = new Test3("aaaaaa");
        test3.strings[0] = new ArrayList<>();
        test3.strings[0].add("sdfasdf");
        test3.strings[1] = null;
        test3.strings[2] = ahoj;
        test3.enume = TestEnum.A;
        test3.map.put(null,"ahaaa");
        test3.map.put(new Test1("asdfasdf"),"bbbb");
        test3.map.put(new ArrayList<String>().add("asdfasfd"),"bbbb");
        test3.map.put(test3.map,"bbbb");
        pm.persist(test3);*/

        Test3 test3 = new Test3("adsfas");
        Test3 test31 = new Test3("bbbbbb");
        HashMap<Object,Object> newMap = new HashMap<>();
        test3.map =newMap;
        test31.map = newMap;

        pm.persist(test3);
        pm.persist(test31);


        Test3 test_loaded = pm.load(1L, Test3.class);
        Test3 test_loaded2 = pm.load(2L, Test3.class);
        System.out.println(test_loaded.toString());
    }
}
