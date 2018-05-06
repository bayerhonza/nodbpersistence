package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.events.EventTypeToListener;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.DefaultClassManagerImpl;
import cz.vutbr.fit.nodbpersistence.core.listeners.AbstractEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.LoadEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.PersistEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.UpdateEventListener;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.core.storage.StorageContext;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

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

    public static final String XML_ELEMENT_ROOT = "class";
    public static final String XML_ELEMENT_OBJECT = "object";
    public static final String XML_ELEMENT_INHERITED = "inherited";
    public static final String XML_ELEMENT_ID_GENERATOR = "id_gen";
    public static final String XML_ATTRIBUTE_OBJECT_ID = "id";
    public static final String XML_ATTRIBUTE_CLASS = "name";

    public static final String XML_ATTRIBUTE_COLL_INST_CLASS = "inst_class";
    public static final String XML_ATTRIBUTE_ISNULL = "is_null";
    public static final String XML_ATTRIBUTE_COLLECTION_ITEM = "item";
    public static final String XML_ATTRIBUTE_FIELD = "field";
    public static final String XML_ATTRIBUTE_FIELD_NAME = "name";
    public static final String XML_ATTRIBUTE_FIELD_REFERENCE = "ref";
    public static final String XML_ATTRIBUTE_INHERITED_CLASS = "inherClass";

    public static final String XML_ATTRIBUTE_ARRAY_SIZE = "size";

    public static final String XML_ELEMENT_MAP_ENTRY = "entry";
    public static final String XML_ELEMENT_MAP_KEY = "key";
    public static final String XML_ELEMENT_MAP_VALUE = "value";

    private Properties properties;

    private final ListenerRegistry listeners = new ListenerRegistry();
    private StorageContext storageContext;
    private final Map<Class<?>, DefaultClassManagerImpl> classClassManagerMap = new HashMap<>();
    private final HashMap<String,Object> referencesCache = new HashMap<>();

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

    public void registerTempReference(String reference, Object object) {
        referencesCache.put(reference,object);
    }

    public Object getObjectByReference(String reference) {
        return referencesCache.get(reference);
    }

    public boolean isReferenceRegistered(String reference) {
        return referencesCache.containsKey(reference);
    }

    public String getReferenceIfPersisted(Object object) {
        DefaultClassManagerImpl defaultClassManager = findClassManager(object.getClass());
        Long objectId = defaultClassManager.isPersistentOrInProgress(object);
        if (objectId == null) {
            return null;
        }
        else {
            return ClassHelper.createReferenceString(object,objectId);
        }

    }
}
