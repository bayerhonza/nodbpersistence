package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.events.UpdateEntityEvent;
import cz.fit.persistence.core.helpers.ReflectionHelper;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.exceptions.PersistenceException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

public class DefaultClassManagerImpl<T> {

    private final Class<T> persistedClass;
    private final Integer classHashCode;


    private HashMap<Integer, Object> persistedObjects = new HashMap<>();
    private final IdGenerator idGenerator;


    private ClassFileHandler fileHandler;


    public DefaultClassManagerImpl(Class<T> persistedClass, Integer classHashCode) {
        this.persistedClass = persistedClass;
        this.idGenerator = new IdGenerator();
        this.classHashCode = classHashCode;
    }

    public String getClassCanonicalName() {
        return persistedClass.getCanonicalName();
    }

    public Class<T> getPersistedClass() {
        return persistedClass;
    }

    public void setFileHandler(ClassFileHandler file) {
        this.fileHandler = file;
    }

    public ClassFileHandler getFileHandler() {
        return fileHandler;
    }

    public void performPersist(PersistEntityEvent persistEvent) throws PersistenceException {
        Object persistedObject = persistEvent.getObject();
        Integer objectId = getObjectId(persistedObject);
        if (persistedObjects.containsKey(objectId)) {
            updateObject(objectId, persistedObject);
        } else {
            persistObject(objectId, persistedObject);
        }

    }

    public void performUpdate(UpdateEntityEvent event) {
    }

    public void performLoad(LoadEntityEvent event) {
    }

    private void launchPersist(PersistEntityEvent persistEvent) {

    }

    private boolean isAlreadyPersisted(Object object) {

        return true;
    }

    private void updateObject(Integer objectId, Object event) {

    }

    private void persistObject(Integer objectId, Object object) throws PersistenceException {
        Object deepCopy = ReflectionHelper.deepCopy(object);
        persistedObjects.put(objectId, deepCopy);
    }

    private Integer getObjectId(Object object) throws PersistenceException {
        try {
            Field objectIdField = Arrays.stream(persistedClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(ObjectId.class))
                    .findFirst()
                    .orElseThrow(() -> new PersistenceException("Object ID not found"));

            boolean accessibility = objectIdField.canAccess(object);
            objectIdField.setAccessible(true);
            Class<?> fieldType = objectIdField.getType();
            Integer objectIdValue = 0;
            if (fieldType.equals(int.class)) {

                objectIdValue = (int) objectIdField.get(object);
                if (objectIdValue == 0) {
                    objectIdValue = null;
                }

            } else if (fieldType.equals(Integer.class)) {
                objectIdValue = (Integer) objectIdField.get(object);
            }
            if (objectIdValue == null) {
                Integer idNext = idGenerator.getNextId();
                objectIdField.set(object, idNext);
                objectIdValue = idNext;
            }
            objectIdField.setAccessible(accessibility);
            return objectIdValue;
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
    }


    private boolean checkIfDirty() {
        return true;
    }

}
