package cz.fit.persistence.core;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.events.EventTypeToListener;
import cz.fit.persistence.core.helpers.HashHelper;
import cz.fit.persistence.core.klass.manager.DefaultClassManagerImpl;
import cz.fit.persistence.core.listeners.AbstractEventListener;
import cz.fit.persistence.core.listeners.LoadEventListener;
import cz.fit.persistence.core.listeners.PersistEventListener;
import cz.fit.persistence.core.listeners.UpdateEventListener;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.core.storage.StorageContext;
import cz.fit.persistence.exceptions.PersistenceCoreException;
import cz.fit.persistence.exceptions.PersistenceException;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
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

    public static final String XML_ELEMENT_ROOT = "class";
    public static final String XML_ELEMENT_OBJECT = "object";
    public static final String XML_ELEMENT_ID_GENERATOR = "id_gen";
    public static final String XML_ATTRIBUTE_OBJECT_ID = "id";
    public static final String XML_ATTRIBUTE_CLASS = "name";

    public static final String XML_ATTRIBUTE_COLL_INST_CLASS = "inst_class";
    public static final String XML_ATTRIBUTE_ISNULL = "is_null";
    public static final String XML_ATTRIBUTE_COLLECITON_ITEM = "item";
    public static final String XML_ATTRIBUTE_FIELD = "field";
    public static final String XML_ATTRIBUTE_FIELD_NAME = "name";
    public static final String XML_ATTRIBUTE_FIELD_REFERENCE = "ref";

    private Properties properties;

    private final ListenerRegistry listeners = new ListenerRegistry();
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

    /**
     * Initialize persistence system in given directory
     *
     * @throws PersistenceCoreException if an internal error occurs
     */
    public void init() throws PersistenceCoreException {
        registerListeners();
        initStorageContext(properties.getProperty(ROOT_DIRECTORY_PROP));
        HashMap<Class<?>, Path> listOfPresentClasses = storageContext.scanForPersistedClass();
        loadClassManagers(listOfPresentClasses);
    }

    /**
     * Assigns a {@link DefaultClassManagerImpl} to given {@code objectClass}. It creates new class manage if class had
     * not been yet persisted.
     *
     * @param objectClass {@link Class} object of persisted class
     * @param <T>         persisted class
     * @return {@link DefaultClassManagerImpl}
     */
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

    public Class<?> getClassFromHash(Integer hashCode) {
        return hashClassMap.get(hashCode);
    }

    private void initStorageContext(String rootDirectory) throws PersistenceCoreException {
        storageContext = new StorageContext(Paths.get(rootDirectory));
        storageContext.init();
    }

    private <T> void createClassManager(Class<T> objectClass) {
        ClassFileHandler classFileHandler = storageContext.createNewClassHandlerFile(objectClass.getCanonicalName());
        DefaultClassManagerImpl<T> classManager = instantiateClassManager(objectClass, false, classFileHandler);
        classClassManagerMap.put(objectClass, classManager);
    }

    private void loadClassManagers(HashMap<Class<?>, Path> classes) {
        classes.forEach((aClass, path) -> {
            ClassFileHandler classFileHandler = storageContext.createClassHandlerByPath(path);
            DefaultClassManagerImpl classManager = instantiateClassManager(aClass, true, classFileHandler);
            classClassManagerMap.put(aClass, classManager);
        });
    }

    private <T> DefaultClassManagerImpl<T> instantiateClassManager(Class<T> tClass, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        return new DefaultClassManagerImpl<>(this, tClass, xmlFileExists, classFileHandler);
    }

    private void registerListeners() {
        listeners.registerListener(PersistEventListener.class, new PersistEventListener());
        listeners.registerListener(UpdateEventListener.class, new UpdateEventListener());
        listeners.registerListener(LoadEventListener.class, new LoadEventListener());
    }

    public String getReferenceIfPersisted(Object object) {
        DefaultClassManagerImpl defaultClassManager = findClassManager(object.getClass());
        Integer objectId = defaultClassManager.isPersistentOrInProgress(object);
        if (objectId == null) {
            return null;
        }
        else {
            return object.getClass().getCanonicalName() + "#" + objectId;
        }

    }
}
