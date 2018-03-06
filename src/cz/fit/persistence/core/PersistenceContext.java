package cz.fit.persistence.core;

import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.klass.manager.ClassManager;
import cz.fit.persistence.core.listeners.PersistEventListenerImpl;
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
    private final String PATH_TO_CONFIG = "./resources/persistence-nodb.xml";

    private Properties properties;

    private Map<Class<EntityEvent>, EventListener> listeners;
    private StorageContext storageContext;
    private Map<Class<?>, ClassManager> classClassManagerMap;

    private PersistenceManagerFactory pmf;

    public PersistenceContext() throws PersistenceException {
        properties = new Properties();
        listeners = new HashMap<>();
        storageContext = new StorageContext();
        classClassManagerMap = new HashMap<>();
        pmf = new PersistenceManagerFactory();


        init();
    }

    private void init() throws PersistenceException {
        try {
            InputStream inputStream = new FileInputStream(Paths.get(PATH_TO_CONFIG).toFile());
            properties.loadFromXML(inputStream);
        } catch (IOException ex) {
            throw new PersistenceException("Error while processing XML config file",ex);
        }

    }

    private void registerListeners() {
        /**
         * register listeners
         */

    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void addToProperties(String entry, String value) {
        properties.put(entry, value);
    }
}
