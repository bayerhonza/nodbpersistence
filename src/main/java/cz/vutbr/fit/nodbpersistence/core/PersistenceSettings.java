package cz.vutbr.fit.nodbpersistence.core;

import java.util.Properties;

/**
 * Configuration class for persistence settings.
 */
public class PersistenceSettings extends Properties {

    private final String ROOT_FS_ELEMENT = "rootDirectory";
    private final String CACHE_SIZE_ELEMENT = "cacheSize";

    public PersistenceSettings setRootPath(String path) {
        put(ROOT_FS_ELEMENT, path);
        return this;
    }

    public PersistenceSettings setCache(Integer cacheSize) {
        put(CACHE_SIZE_ELEMENT, cacheSize);
        return this;
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
