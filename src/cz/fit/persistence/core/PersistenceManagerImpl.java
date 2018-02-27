package cz.fit.persistence.core;


import cz.fit.persistence.core.storage.StorageContext;
import cz.fit.persistence.hash.HashHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


public class PersistenceManagerImpl {

    private PersistenceConfiguration config;

    private StorageContext storageContext;

    private HashMap<Integer,ClassPersist> hashMap;

    public PersistenceManagerImpl(String path, CompilationMethod ZIPmethod) {
        config = new PersistenceConfiguration(path,ZIPmethod);
        hashMap = new HashMap<>();


    }

    PersistenceManagerImpl(String path) {
        config = new PersistenceConfiguration(path);
    }

    public void persist(Object ob) throws Exception {
        if (ob == null) {
            throw new NullPointerException();
        }
        Integer obClassHashCode = HashHelper.getHashFromClass(ob.getClass());
        ClassPersist classPersist;
        if (hashMap.containsKey(obClassHashCode)) {
            classPersist = hashMap.get(obClassHashCode);
        } else {
            classPersist = new ClassPersist(ob.getClass());
            hashMap.put(obClassHashCode, classPersist);
        }
        persistKnownClass(ob, classPersist);

    }

    private void persistKnownClass(Object ob, ClassPersist classPersist) throws IOException {
        Path klassPath = Paths.get(config.getPath());
        if (!Files.exists(klassPath)) {
            Files.createDirectories(klassPath);
        }






    }


    }
