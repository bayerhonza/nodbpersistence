import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactory;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManagerFactoryBuilder;
import cz.vutbr.fit.nodbpersistence.core.PersistenceSettings;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class HelloWorldNoDbPersistence {

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
        }

        //PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        /*Test3 test3 = new Test3();
        Set<Test1> listTest1 = test3.getListTest1();
        for (int i = 0; i < count; i++) {
            Test1 test = new Test1()
                    .setNumber(i);
            listTest1.add(test);
            List<List<Test2>> list = test.getList();
            for (int j = 0; j < count; j++) {
                List<Test2> nextList = new ArrayList<>();
                list.add(nextList);
                for (int k = 0; k < count; k++) {
                    Test2 test2 = new Test2();
                    test2.getList().add(i + "/" + j + "/" + k);
                    nextList.add(test2);
                }
            }
        }
        long before = System.nanoTime();
        pm.persist(test3);
        long elapsedTime = System.nanoTime() - before;*/

        /*Test1 test1 = new Test1("test11");
        test1.setText("test11");
        test1.setTest1(test1);

        Test2 test2 = new Test2();
        test2.setText("test22");

        Set<Test2> setTest2 = new HashSet<>();
        setTest2.add(test2);
        setTest2.add(test2);
        setTest2.add(test2);
        setTest2.add(test2);

        List<Test1> listTest1 = new ArrayList<>();
        listTest1.add(test1);
        listTest1.add(test1);
        listTest1.add(test1);
        listTest1.add(test1);

        test1.setSet(setTest2);
        test2.setList(listTest1);


        pm.persist(test1);
        pm.persist(test2);

        Test1 test11 = pm.load(1,Test1.class);
        Test2 test22 = pm.load(1,Test2.class);
        System.out.println(test11);
        System.out.println(test22);

        Test1 test1a = test11;*/

        System.out.println(System.getProperty("java.vm.name"));
        System.out.println(System.getProperty("java.home"));
        System.out.println(System.getProperty("java.vendor"));
        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.specification.vendor"));

        Test3 test3inheritance = (Test3) ClassHelper.instantiateClass(Test3.class);

        System.out.println(System.identityHashCode(test3inheritance));
        //test3inheritance.objectId = "aaaaa";
        //((Test1) test3inheritance).objectId = "bbbbbb";
        System.out.println(test3inheritance.objectId);
        System.out.println(((Test1) test3inheritance).objectId);

        List<Field> fields = new ArrayList<>();
        Class<?> klazz = Test3.class;
        while (klazz != Object.class) {
            fields.addAll(Arrays.asList(klazz.getDeclaredFields()));
            klazz = klazz.getSuperclass();
        }
        fields.forEach(field -> {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();
                Class<?> type = field.getType();
                if (fieldName.equals("objectId") && type == String.class) {
                    String str = "jjjjj";
                    field.set(test3inheritance, str);
                }
                System.out.println(field.getDeclaringClass() + ":" + field.getType() + ":" + field.getName() + "=" + field.get(test3inheritance) == null ? "null" : field.get(test3inheritance).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });


        //System.out.println("elapsed time: " + TimeUnit.NANOSECONDS.toMillis(elapsedTime)/1000.0 + "s");

        /*Test1 test = new Test1();
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
        //pm.persist(test12);

        /*Test3 test1111 = pm.load(1, Test3.class);
        System.out.println(test1111);*/

        /*Test1 test_loaded = (Test1) pm.load(1, Test1.class);
        System.out.println(test_loaded.toString());*/
    }
}
