package cz.fit.persistence.core.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;

public class ClassFileHandler {

    private final File XMLClassFile;

    public ClassFileHandler(Path pathToFile) {
        this.XMLClassFile = pathToFile.toFile();
    }

    public FileOutputStream getXMLOutputStream() throws FileNotFoundException {
        return new FileOutputStream(XMLClassFile);
    }
}
