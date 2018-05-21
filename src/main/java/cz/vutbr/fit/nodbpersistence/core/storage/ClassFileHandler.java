package cz.vutbr.fit.nodbpersistence.core.storage;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Class for XML files.
 */
public class ClassFileHandler {

    private final File xmlClassFile;

    ClassFileHandler(Path pathToFile) {
        this.xmlClassFile = pathToFile.toFile();
    }

    /**
     * Creates output stream for the given file
     * @return output stream
     * @throws PersistenceException if file not found
     */
    public OutputStream getXMLOutputStream() {
        try {
            return new FileOutputStream(xmlClassFile);
        } catch (FileNotFoundException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Getter for file
     * @return file
     */
    public File getXmlClassFile() {
        return xmlClassFile;
    }
}
