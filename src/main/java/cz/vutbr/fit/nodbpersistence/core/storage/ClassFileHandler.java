package cz.vutbr.fit.nodbpersistence.core.storage;

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
     * @throws FileNotFoundException
     */
    public OutputStream getXMLOutputStream() throws FileNotFoundException {
        return new FileOutputStream(xmlClassFile);
    }

    /**
     * Getter for file
     * @return file
     */
    public File getXmlClassFile() {
        return xmlClassFile;
    }
}
