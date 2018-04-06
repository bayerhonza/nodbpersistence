package cz.fit.persistence.core.storage;

import cz.fit.persistence.core.klass.manager.DefaultClassManagerImpl;
import cz.fit.persistence.exceptions.PersistenceCoreException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StorageContext {

    private final Path rootDirectory;
    private final boolean asRoot;

    private final Map<DefaultClassManagerImpl, ClassFileHandler> classFileHandlers = new HashMap<>();

    public StorageContext(String rootDirectory) {
        this(rootDirectory, false);
    }

    private StorageContext(String rootDirectory, boolean asRoot) {
        this.rootDirectory = Paths.get(rootDirectory);
        this.asRoot = false;
    }

    public void init() throws PersistenceCoreException {
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException ex) {
            throw new PersistenceCoreException(ex);
        }
    }

    public ClassFileHandler getClassHandler(DefaultClassManagerImpl classManager) {
        ClassFileHandler classFileHandler = classFileHandlers.get(classManager);
        if (classFileHandler == null) {
            classFileHandler = new ClassFileHandler(Paths.get(rootDirectory.toString() + "/" + classManager.getClassCanonicalName() + ".xml"));
            classFileHandlers.put(classManager, classFileHandler);
        }
        return classFileHandler;
    }
}
