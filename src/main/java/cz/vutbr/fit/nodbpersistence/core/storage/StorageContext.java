package cz.vutbr.fit.nodbpersistence.core.storage;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class StorageContext {

    private final Path rootDirectory;

    /**
     * Storage context
     *
     * @param rootDirectory root directory for persistence
     */
    public StorageContext(Path rootDirectory) {
        this(rootDirectory, false);

    }

    /**
     * Constructor for restoring StorageContext object from defined directory
     *
     * @param rootDirectory Directory file of context to be restored
     */
    private StorageContext(Path rootDirectory, boolean restore) {
        this.rootDirectory = rootDirectory;
    }

    public void init() throws PersistenceCoreException {
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException ex) {
            throw new PersistenceCoreException(ex);
        }
    }

    public ClassFileHandler createNewClassHandlerFile(String classCanonicalName) {
        return createClassHandlerByPath(Paths.get(rootDirectory.toString() + "/" + classCanonicalName + ".xml"));
    }

    public ClassFileHandler createClassHandlerByPath(Path path) {
        return new ClassFileHandler(path);
    }

    public HashMap<Class<?>, Path> scanForPersistedClass() {
        HashMap<Class<?>, Path> classList = new HashMap<>();
        File[] classDirs = rootDirectory.toFile().listFiles(File::isFile);
        if (classDirs == null) {
            return classList;
        }
        for (File classDir : classDirs) {
            String dirName = classDir.toPath().getFileName().toString();

            // delete the extension
            dirName = dirName.substring(0, dirName.lastIndexOf("."));
            try {
                // check if class is loaded
                Class<?> klazz = Class.forName(dirName);
                classList.put(klazz, classDir.toPath());

            } catch (ClassNotFoundException ignored) {
                // not recognised class, skipp it
            }
        }
        return classList;
    }
}
