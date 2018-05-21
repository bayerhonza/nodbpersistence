package cz.vutbr.fit.nodbpersistence.core.storage;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Class for handling the storage of objects.
 */
public class StorageContext {

    private final Path rootDirectory;

    /**
     * Storage context
     *
     * @param rootDirectory root directory for persistence
     */
    public StorageContext(Path rootDirectory) {
        this.rootDirectory = rootDirectory;

    }

    /**
     * Initializing, creating defined directory.
     * @throws PersistenceCoreException if directory cannot be created.
     */
    public void init() throws PersistenceCoreException {
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException ex) {
            throw new PersistenceCoreException(ex);
        }
    }

    /**
     * Creates {@link ClassFileHandler} for the given class name as an XML file.
     * @param className class name as a string
     * @return class file handler
     */
    public ClassFileHandler createNewClassHandlerFile(String className) {
        return createClassHandlerByPath(Paths.get(rootDirectory.toString() + "/" + className + ".xml"));
    }

    /**
     * Creates {@link ClassFileHandler} based on given {@code path}.
     * @param path path to XML file
     * @return class file handler
     */
    public ClassFileHandler createClassHandlerByPath(Path path) {
        return new ClassFileHandler(path);
    }

    /**
     * Scans root directory for class files. It accepts the file names and checks if their name represents
     * any existing loaded class. If so, it adds it to a {@code HashMap}.
     * @return map of existing class files.
     */
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
                // not recognised class, skip it
            }
        }
        return classList;
    }


}
