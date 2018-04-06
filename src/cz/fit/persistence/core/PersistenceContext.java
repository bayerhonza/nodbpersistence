package cz.fit.persistence.core;

import cz.fit.persistence.core.events.EventTypeToListener;
import cz.fit.persistence.core.helpers.HashHelper;
import cz.fit.persistence.core.klass.manager.DefaultClassManagerImpl;
import cz.fit.persistence.core.listeners.AbstractEventListener;
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
    public static final String PATH_TO_CONFIG = "./resources/persistence-nodb.xml";

    public static final String ROOT_DIRECTORY_PROP = "rootDirectory";
    public static final String CACHE_SIZE_PROP = "cacheSize";

    public static final String ROOT_XML_ELEMENT = "persistedClass";
    public static final String CLASS_ATTRIBUT = "className";
    public static final String OBJECT_XML_ELEMENT = "persistedObject";

    private Properties properties;

    private final RegisteredListeners listeners = new RegisteredListeners();
    private StorageContext storageContext;
    private final Map<Class<?>, DefaultClassManagerImpl> classClassManagerMap = new HashMap<>();
    private final Map<Integer, Class<?>> hashClassMap = new HashMap<>();

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
    }

    public <T extends AbstractEventListener> T getListenerToEvent(EventTypeToListener<T> eventTypeToListener) {
        return listeners.findListener(eventTypeToListener);

    }

    private void loadXMLProperties() throws PersistenceException {
        try {
            InputStream inputStream = new FileInputStream(Paths.get(PATH_TO_CONFIG).toFile());
            properties.loadFromXML(inputStream);
        } catch (IOException ex) {
            throw new PersistenceException("Error while processing XML config file", ex);
        }

    }

    public void init() throws PersistenceCoreException {
        registerListeners();
        initStorageContext(properties.getProperty(ROOT_DIRECTORY_PROP));

    }

    @SuppressWarnings({"unchecked"})
    public <T> DefaultClassManagerImpl<T> findClassManager(Class<T> objectClass) {
        if (!classClassManagerMap.containsKey(objectClass)) {
            createClassManager(objectClass);
        }
        return classClassManagerMap.get(objectClass);
    }

    public StorageContext getStorageContext() {
        return storageContext;
    }


    private void initStorageContext(String rootDirectory) throws PersistenceCoreException {
        storageContext = new StorageContext(rootDirectory);
        storageContext.init();
    }

    private <T> void createClassManager(Class<T> objectClass) {
        DefaultClassManagerImpl<T> classManager = new DefaultClassManagerImpl<>(objectClass, hashClass(objectClass));
        classManager.setFileHandler(storageContext.getClassHandler(classManager));
        classClassManagerMap.put(objectClass, classManager);
    }

    private <T> Integer hashClass(Class<T> objectClass) {
        Integer classHashCode = HashHelper.getHashFromClass(objectClass);
        hashClassMap.put(classHashCode, objectClass);
        return classHashCode;
    }

    private void registerListeners() {
        listeners.registerListener(PersistEventListener.class, new PersistEventListener());
        listeners.registerListener(UpdateEventListener.class, new UpdateEventListener());
        listeners.registerListener(LoadEventListener.class, new LoadEventListener());
    }
}
