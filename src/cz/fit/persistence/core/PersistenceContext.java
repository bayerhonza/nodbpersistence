package cz.fit.persistence.core;

import cz.fit.persistence.core.conf.Properites;
import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.core.klass.manager.ClassManager;
import cz.fit.persistence.core.storage.StorageContext;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class PersistenceContext {

    private Properites properites;

    private Map<EntityEvent, EventListener> listeners;
    private StorageContext storageContext;
    private Map<Class<?>, ClassManager> classClassManagerMap;

    private PersistenceManagerFactory pmf;

    public PersistenceContext() {
    }

    public void init() {


        listeners = new HashMap<>();
        storageContext = new StorageContext();
        classClassManagerMap = new HashMap<>();
        pmf = new PersistenceManagerFactory();
    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setProperites(Properites properites) {
        this.properites = properites;
    }

    public void addToProperties(String entry, String value) {
        properites.put(entry, value);
    }
}
