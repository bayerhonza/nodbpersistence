package cz.fit.persistence.core.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class ClassFileHandler {

    private final File xmlClassFile;

    public ClassFileHandler(Path pathToFile) {
        this.xmlClassFile = pathToFile.toFile();
    }

    public OutputStream getXMLOutputStream() throws FileNotFoundException {
        return new FileOutputStream(xmlClassFile);
    }
}
