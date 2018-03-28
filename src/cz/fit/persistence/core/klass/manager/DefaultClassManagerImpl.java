package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.events.UpdateEntityEvent;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.exceptions.PersistenceException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

public class DefaultClassManagerImpl<T> {

    private final Class<T> persistedClass;
    private HashSet<T> persistedObjects = new HashSet<>();
    private final IdGenerator idGenerator;

    private ClassFileHandler fileHandler;


    public DefaultClassManagerImpl(Class<T> persistedClass, IdGenerator idGenerator) {
        this.persistedClass = persistedClass;
        this.idGenerator = idGenerator;
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

    public void performPersist(PersistEntityEvent persistEvent) {
        Object persistedObject = persistEvent.getObject();
        Integer objectId = getObjectId(persistedObject);
    }

    public void performUpdate(UpdateEntityEvent event) {
    }

    public void performLoad(LoadEntityEvent event) {
    }

    private boolean isAlreadyPersisted(Object object) {

        return true;
    }

    private Integer getObjectId(Object object) throws PersistenceException {
        try {
            Field objectIdField = Arrays.stream(persistedClass.getDeclaredFields())
                    .peek(field -> System.out.println("filtered " + field.getName()))
                    .filter(field -> field.isAnnotationPresent(ObjectId.class))
                    .findFirst()
                    .orElseThrow(() -> new PersistenceException("Object ID not found"));

            objectIdField.setAccessible(true);
            Class<?> fieldType = objectIdField.getType();
            Integer objectIdValue = 0;
            if (fieldType.equals(int.class)) {

                objectIdValue = (int) objectIdField.get(object);

            } else if (fieldType.equals(Integer.class)) {
                objectIdValue = (Integer) objectIdField.get(object);
            }
            if (objectIdValue == 0) {
                objectIdField.set(object, idGenerator.getNextId());
            }

            return objectIdValue;
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
    }


    private boolean checkIfDirty() {
        return true;
    }

}
