package cz.fit.persistence.core;


import cz.fit.persistence.hash.HashHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class PersistenceManagerImpl {

    private PersistenceConfiguration config;

    private HashMap<Integer,ClassHashMap> hashMap;

    public PersistenceManagerImpl(String path, CompilationMethod ZIPmethod) {
        config = new PersistenceConfiguration(path,ZIPmethod);
        hashMap = new HashMap<>();
    }

    public PersistenceManagerImpl(String path) {
        config = new PersistenceConfiguration(path);
    }

    public void persist(Object ob) throws Exception {
        if (ob == null) {
            throw new NullPointerException();
        }
        Integer obClassHashCode = HashHelper.getHashFromClass(ob.getClass());
        if (hashMap.containsKey(obClassHashCode)) {
            ClassHashMap classHashMap = hashMap.get(obClassHashCode);
            persistKnownClass(ob,classHashMap);
        }
        Class<?> klass = ob.getClass();
        Constructor<?>[] publicConstructors = klass.getConstructors();
        Field[] publicFields = ob.getClass().getFields();

        System.out.println(Arrays.toString(publicConstructors));
        System.out.println(klass.getCanonicalName());
        System.out.println(Arrays.toString(publicFields));

    }

    private void persistKnownClass(Object ob, ClassHashMap classHashMap) {
        System.out.println(classHashMap.toString());

    }


    }
