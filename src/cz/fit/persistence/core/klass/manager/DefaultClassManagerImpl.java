package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.PersistenceContext;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.events.UpdateEntityEvent;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.exceptions.PersistenceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

public class DefaultClassManagerImpl<T> {

    private final Class<T> persistedClass;
    private final Integer classHashCode;


    private HashMap<Integer, Object> persistedObjects = new HashMap<>();
    private final IdGenerator idGenerator;


    private ClassFileHandler fileHandler;
    private Document xmlDocument;
    private Element rootElement;
    Transformer transformer;


    public DefaultClassManagerImpl(Class<T> persistedClass, Integer classHashCode) {
        this.persistedClass = persistedClass;
        this.idGenerator = new IdGenerator();
        this.classHashCode = classHashCode;
        initXMLDocument(persistedClass);
        initXMLTransformer();
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


    private void initXMLDocument(Class<T> persistedClass) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new PersistenceException(e);
        }
        xmlDocument = documentBuilder.newDocument();

        rootElement = xmlDocument.createElement(PersistenceContext.ROOT_XML_ELEMENT);
        Attr className = xmlDocument.createAttribute(PersistenceContext.CLASS_ATTRIBUT);
        className.setValue(persistedClass.getCanonicalName());
        rootElement.setAttributeNode(className);
        xmlDocument.appendChild(rootElement);
    }

    private void initXMLTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new PersistenceException(e);
        }

    }

    private void launchPersist(PersistEntityEvent persistEvent) {

    }

    private boolean isAlreadyPersisted(Object object) {

        return true;
    }

    private void updateObject(Integer objectId, Object event) {

    }

    private void persistObject(Integer objectId, Object object) throws PersistenceException {

        try {
            System.out.println(objectId.toString());

            Element persistedObject = xmlDocument.createElement(PersistenceContext.OBJECT_XML_ELEMENT);
            rootElement.appendChild(persistedObject);

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                Element xmlField = xmlDocument.createElement(field.getName());
                boolean accesible = field.canAccess(object);
                field.setAccessible(true);
                xmlField.appendChild(xmlDocument.createTextNode(field.get(object) == null ? null : field.get(object).toString()));
                persistedObject.appendChild(xmlField);
                field.setAccessible(accesible);
            }
            saveXMLDocument();

        } catch (IllegalAccessException | FileNotFoundException | TransformerException e) {
            throw new PersistenceException(e);
        }

    }

    private void saveXMLDocument() throws TransformerException, FileNotFoundException {
        DOMSource source = new DOMSource(xmlDocument);
        StreamResult result = new StreamResult(fileHandler.getXMLOutputStream());
        transformer.transform(source, result);

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
