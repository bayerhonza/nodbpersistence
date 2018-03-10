package cz.fit.persistence.core;

import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.core.klass.manager.ClassManager;
import cz.fit.persistence.core.storage.StorageContext;
import cz.fit.persistence.exceptions.PersistenceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PersistenceContext {

    /**
     * Default path to XML config file
     */
    private static final String PATH_TO_CONFIG = "./resources/persistence-nodb.xml";

    public static final String ROOT_DIRECTORY_PROP = "rootDirectory";
    public static final String CACHE_SIZE_PROP = "cacheSize";

    private Properties properties;

    private Map<Class<EntityEvent>, EventListener> listeners;
    private StorageContext storageContext;
    private Map<Class<?>, ClassManager> classClassManagerMap;

    /**
     * Constructor for PersistenceContext without predefined properties
     */
    PersistenceContext() {
        this(null);

    }

    /**
     * Constructor for PersistenceContext with predefinde properties created by {@link PersistenceSettings}
     *
     * @param properties predefined properties
     * @throws PersistenceException if IO error occurs while processing XML properties file
     */
    PersistenceContext(Properties properties) throws PersistenceException {
        if (properties == null) {
            loadXMLProperties();
        } else {
            this.properties = properties;
        }
        listeners = new HashMap<>();
        classClassManagerMap = new HashMap<>();


    }

    private void loadXMLProperties() throws PersistenceException {
        try {
            InputStream inputStream = new FileInputStream(Paths.get(PATH_TO_CONFIG).toFile());
            properties.loadFromXML(inputStream);
        } catch (IOException ex) {
            throw new PersistenceException("Error while processing XML config file",ex);
        }

        storageContext = new StorageContext();

    }

    private void init() {

    }

    private void registerListeners() {
        /*
         * register listeners
         */

    }
}
