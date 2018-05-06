package cz.vutbr.fit.nodbpersistence.core;

import java.util.Properties;

public class PersistenceSettings extends Properties {

    private final String ROOTFS_ELEMENT = "rootDirectory";
    private final String CACHESIZE_ELEMENT = "cacheSize";

    public PersistenceSettings setRootPath(String path) {
        put(ROOTFS_ELEMENT, path);
        return this;
    }

    public PersistenceSettings setCache(Integer cacheSize) {
        put(CACHESIZE_ELEMENT, cacheSize);
        return this;
    }

    public String getRootPath() {
        return getProperty(ROOTFS_ELEMENT);
    }

    public String getCacheSize() {
        return getProperty(CACHESIZE_ELEMENT);
    }

    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        PersistenceContext persistenceContext = new PersistenceContext();
        return new PersistenceManagerFactory(persistenceContext);
    }

}
