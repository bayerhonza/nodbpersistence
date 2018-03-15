package cz.fit.persistence.core.storage;

import cz.fit.persistence.core.klass.manager.ClassManager;
import cz.fit.persistence.exceptions.PersistenceCoreException;

import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class StorageContext {

    private Path rootDirectory;
    private boolean asRoot;

    private HashMap<ClassManager, File> classManagerOutputStreamHashMap;
    private HashMap<ClassManager, File> classManagerInputStreamHashMap;

    public StorageContext(String rootDirectory) {
        this(rootDirectory, false);
    }

    public StorageContext(String rootDirectory, boolean asRoot) {
        this.rootDirectory = Paths.get(rootDirectory);
        this.asRoot = asRoot;
    }

    public void init() throws PersistenceCoreException {
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException ex) {
            throw new PersistenceCoreException(ex);
        }
    }

    public FileOutputStream getOrAddOutputStream(ClassManager classManager) throws PersistenceCoreException {
        if (!classManagerOutputStreamHashMap.containsKey(classManager)) {
            registerClassManager(classManager);
        }
        try {
            return new FileOutputStream(classManagerOutputStreamHashMap.get(classManager));
        } catch (FileNotFoundException e) {
            throw new PersistenceCoreException(e);
        }
    }

    private void registerClassManager(ClassManager classManager) {
        classManagerOutputStreamHashMap.put(classManager, new File(classManager.getClassName()));
    }
}
