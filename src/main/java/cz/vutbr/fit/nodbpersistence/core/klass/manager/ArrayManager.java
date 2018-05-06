package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;

public class ArrayManager extends AbstractClassManager{

    public ArrayManager(PersistenceContext persistenceContext, boolean xmlFileExists, Class<?> persistedClass, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler);
    }

    @Override
    void refreshPersistedObjects() {

    }

    @Override
    public Object performLoad(Long objectId) {
        return null;
    }

    @Override
    public void performPersist(PersistEntityEvent persistEntityEvent) {

    }

    @Override
    public Object getObjectById(Long objectId) {
        return null;
    }

    @Override
    public Long getObjectId(Object object) {
        return null;
    }

    @Override
    public Object getObjectByReference(String reference) throws ClassNotFoundException {
        return null;
    }

    @Override
    public Long isPersistentOrInProgress(Object object) {
        return null;
    }

    @Override
    public void initXMLDocument(Class<?> persistedClass) {

    }
}
