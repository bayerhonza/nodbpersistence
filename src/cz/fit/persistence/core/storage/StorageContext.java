package cz.fit.persistence.core.storage;

import cz.fit.persistence.core.klass.manager.ClassManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;

public class StorageContext {

    private Path rootDirectory;
    private boolean asRoot;

    private HashMap<ClassManager,OutputStream> classManagerOutputStreamHashMap;
    private HashMap<ClassManager,InputStream> classManagerInputStreamHashMap;



}
