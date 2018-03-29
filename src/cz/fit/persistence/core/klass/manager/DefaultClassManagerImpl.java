package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.events.UpdateEntityEvent;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.exceptions.PersistenceException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

public class DefaultClassManagerImpl<T> {

    private final Class<T> persistedClass;
    private HashMap<Integer, T> persistedObjects = new HashMap<>();
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

    private void persistObject(Integer objectId, Object object) {
        try {
            JAXBContext jc = JAXBContext.newInstance(persistedClass);

            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            JAXBElement<T> rootElement = new JAXBElement<T>(new QName(getClassCanonicalName()), persistedClass, (T) object);
            marshaller.marshal(rootElement, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    private Integer getObjectId(Object object) throws PersistenceException {
        try {
            Field objectIdField = Arrays.stream(persistedClass.getDeclaredFields())
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
