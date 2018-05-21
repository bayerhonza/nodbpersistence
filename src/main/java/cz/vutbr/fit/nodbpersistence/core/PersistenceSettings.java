package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration class for persistence settings.
 */
public class PersistenceSettings extends Properties {

    private final String ROOT_FS_ELEMENT = "rootDirectory";
    private final String CACHE_SIZE_ELEMENT = "cacheSize";

    private final String RESOURCE_PATH = "resources/";
    private final String PROPERTIES_FILE_NAME = "nodbpersistence.xml";

    public PersistenceSettings setRootPath(String path) {
        put(ROOT_FS_ELEMENT, path);
        return this;
    }

    public PersistenceSettings setCache(Integer cacheSize) {
        put(CACHE_SIZE_ELEMENT, cacheSize);
        return this;
    }

    public void loadFromPropertiesFile() {
        loadFromPropertiesFile(RESOURCE_PATH + PROPERTIES_FILE_NAME);
    }

    public void loadFromPropertiesFile(String propertiesPath) {
        try {
            this.loadFromXML(new FileInputStream(propertiesPath));
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

    public String getRootPath() {
        return getProperty(ROOT_FS_ELEMENT);
    }

    public String getCacheSize() {
        return getProperty(CACHE_SIZE_ELEMENT);
    }

    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        PersistenceContext persistenceContext = new PersistenceContext();
        return new PersistenceManagerFactory(persistenceContext);
    }

}
