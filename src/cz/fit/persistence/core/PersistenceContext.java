package cz.fit.persistence.core;

import cz.fit.persistence.core.events.AbstractEntityEvent;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.events.UpdateEntityEvent;
import cz.fit.persistence.core.klass.manager.ClassManager;
import cz.fit.persistence.core.klass.manager.DefaultClassManagerImpl;
import cz.fit.persistence.core.listeners.EventListener;
import cz.fit.persistence.core.listeners.LoadEventListener;
import cz.fit.persistence.core.listeners.PersistEventListener;
import cz.fit.persistence.core.listeners.UpdateEventListener;
import cz.fit.persistence.core.storage.StorageContext;
import cz.fit.persistence.exceptions.PersistenceCoreException;
import cz.fit.persistence.exceptions.PersistenceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
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

    private Map<Class<? extends AbstractEntityEvent>, EventListener> listeners;
    private StorageContext storageContext;
    private Map<Class<?>, DefaultClassManagerImpl> classClassManagerMap;

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

    public EventListener getListenerToEvent(AbstractEntityEvent event) {
        return listeners.get(event.getClass());

    }

    private void loadXMLProperties() throws PersistenceException {
        try {
            InputStream inputStream = new FileInputStream(Paths.get(PATH_TO_CONFIG).toFile());
            properties.loadFromXML(inputStream);
        } catch (IOException ex) {
            throw new PersistenceException("Error while processing XML config file",ex);
        }

    }

    public void init() throws PersistenceCoreException {
        registerListeners();
        initStorageContext(properties.getProperty(ROOT_DIRECTORY_PROP));
    }

    public <T> DefaultClassManagerImpl<T> findClassManager(T object) {
        if (!classClassManagerMap.containsKey(object.getClass())) {
            createClassManager(object);
        }
        return classClassManagerMap.get(object);
    }

    private void registerListeners() {
        listeners.put(
                PersistEntityEvent.class,
                new PersistEventListener()
        );
        listeners.put(
                UpdateEntityEvent.class,
                new UpdateEventListener()
        );
        listeners.put(
                LoadEntityEvent.class,
                new LoadEventListener()
        );
    }

    private void initStorageContext(String rootDirectory) throws PersistenceCoreException {
        storageContext = new StorageContext(rootDirectory);
        storageContext.init();
    }

    private <T> void createClassManager(T object) {
        classClassManagerMap.put(object.getClass(), new DefaultClassManagerImpl<>(object));
    }
}
