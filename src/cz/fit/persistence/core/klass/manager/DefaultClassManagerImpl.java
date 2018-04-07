package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.PersistenceContext;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.helpers.ConvertStringToType;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.exceptions.PersistenceException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultClassManagerImpl<T> {
    private final Class<T> persistedClass;
    private final Integer classHashCode;

    private final Field objectIdField;


    private HashMap<Integer, Node> persistedObjects = new HashMap<>();
    private final IdGenerator idGenerator;


    private ClassFileHandler fileHandler;
    private Document xmlDocument;
    private Element rootElement;
    Transformer transformer;
    XPathFactory xPathFactory = XPathFactory.newInstance();


    public DefaultClassManagerImpl(Class<T> persistedClass, Integer classHashCode) {
        this.persistedClass = persistedClass;
        this.idGenerator = new IdGenerator();
        this.classHashCode = classHashCode;
        this.objectIdField = getObjectIdField();

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
        if (isAlreadyPersisted(objectId)) {
            updateObject(objectId, persistedObject);
        } else {
            persistObject(objectId, persistedObject);
        }
    }

    public Node queryXMLModel(String xmlPathQuery) {
        XPath xPath = xPathFactory.newXPath();
        Node objectNode;
        try {
            XPathExpression expr = xPath.compile(xmlPathQuery);
            objectNode = (Node) expr.evaluate(xmlDocument, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new PersistenceException(e);
        }
        return objectNode;
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

        rootElement = xmlDocument.createElement(PersistenceContext.XML_ROOT_ELEMENT);
        Attr className = xmlDocument.createAttribute(PersistenceContext.XML_CLASS_ATTRIBUT);
        className.setValue(persistedClass.getCanonicalName());
        rootElement.setAttributeNode(className);
        xmlDocument.appendChild(rootElement);
    }

    private void initXMLTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            throw new PersistenceException(e);
        }

    }

    private boolean isAlreadyPersisted(Object object) {

        return true;
    }

    private void updateObject(Integer objectId, Object object) {
        boolean isDirty = checkIfDirty(objectId, object);
        if (isDirty) {
            Field[] fields = persistedClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ObjectId.class)) {
                    continue;
                }
                Node node = getObjectAttributByName(objectId, field.getName());
                try {
                    // TODO update collections and non primitive types
                    boolean accessible = field.canAccess(object);
                    field.setAccessible(true);
                    node.getFirstChild().setNodeValue(field.get(object).toString());
                    field.setAccessible(accessible);
                } catch (IllegalAccessException e) {
                    throw new PersistenceException(e);
                }
            }
            try {
                flushXMLDocument();
            } catch (TransformerException | FileNotFoundException e) {
                throw new PersistenceException(e);
            }
        }
    }

    private void persistObject(Integer objectId, Object object) throws PersistenceException {

        try {
            Element persistedObject = xmlDocument.createElement(PersistenceContext.XML_OBJECT_ELEMENT);
            rootElement.appendChild(persistedObject);

            persistedObject.setAttribute(PersistenceContext.XML_OBJECT_ID, objectId.toString());

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(ObjectId.class)) {
                    continue;
                }
                Element xmlField = xmlDocument.createElement(field.getName());
                boolean accessible = field.canAccess(object);
                field.setAccessible(true);

                // TODO support non primitive types and collections
                xmlField.appendChild(xmlDocument.createTextNode(field.get(object) == null ? null : field.get(object).toString()));
                persistedObject.appendChild(xmlField);
                field.setAccessible(accessible);
            }
            persistedObjects.put(objectId, persistedObject);
            flushXMLDocument();

        } catch (IllegalAccessException | FileNotFoundException | TransformerException e) {
            throw new PersistenceException(e);
        }

    }

    private void flushXMLDocument() throws TransformerException, FileNotFoundException {
        DOMSource source = new DOMSource(xmlDocument);
        StreamResult result = new StreamResult(fileHandler.getXMLOutputStream());
        transformer.transform(source, result);

    }

    private Integer getObjectId(Object object) throws PersistenceException {
        try {
            boolean accessibility = objectIdField.canAccess(object);
            objectIdField.setAccessible(true);
            Class<?> fieldType = objectIdField.getType();
            Integer objectIdValue = 0;

            // TODO support more type of objectId
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

    private Node getObjectNodeById(Integer objectId) {
        return queryXMLModel("/" + PersistenceContext.XML_ROOT_ELEMENT + "/" + PersistenceContext.XML_OBJECT_ELEMENT + "[@" + PersistenceContext.XML_OBJECT_ID + "=" + objectId + "]");

    }

    private Node getObjectAttributByName(Integer objectId, String name) {
        System.out.println("/" + PersistenceContext.XML_ROOT_ELEMENT + "/" + PersistenceContext.XML_OBJECT_ELEMENT + "[@" + PersistenceContext.XML_OBJECT_ID + "=" + objectId + "]/" + name);
        return queryXMLModel("/" + PersistenceContext.XML_ROOT_ELEMENT + "/" + PersistenceContext.XML_OBJECT_ELEMENT + "[@" + PersistenceContext.XML_OBJECT_ID + "=" + objectId + "]/" + name);
    }

    private Object getObjectById(Integer objectId) {
        Node objectNode = getObjectNodeById(objectId);

        T newObj = null;
        try {
            newObj = persistedClass.getConstructor().newInstance();

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        // setting of objectID
        try {
            boolean accessibilityId = objectIdField.canAccess(newObj);
            objectIdField.setAccessible(true);
            Type typeId = objectIdField.getType();
            String idToString = objectNode.getAttributes().getNamedItem(PersistenceContext.XML_OBJECT_ID).getNodeValue();
            objectIdField.set(newObj, ConvertStringToType.convertStringToType(typeId, idToString));
            objectIdField.setAccessible(accessibilityId);
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }

        // setting of all the other attributes
        NodeList attributes = objectNode.getChildNodes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String fieldName = attribute.getNodeName();
            try {
                Field field = persistedClass.getDeclaredField(fieldName);
                boolean accessibility = field.canAccess(newObj);
                field.setAccessible(true);

                // TODO support collections and non primitive types
                String fieldValue = attribute.getFirstChild().getNodeValue();
                field.set(newObj, ConvertStringToType.convertStringToType(field.getType(), fieldValue));
                field.setAccessible(accessibility);
            } catch (NoSuchFieldException e) {
                throw new PersistenceException(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }
        return newObj;
    }

    private boolean isAlreadyPersisted(Integer objectId) {
        return persistedObjects.containsKey(objectId);
    }

    private Field getObjectIdField() {
        List<Field> objectIdFields = Arrays.stream(persistedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ObjectId.class))
                .collect(Collectors.toList());
        Field objectIdField;

        if (objectIdFields.size() > 1) {
            throw new PersistenceException("Multiple ObjectId defined.");
        } else if (objectIdFields.size() == 0) {
            throw new PersistenceException("No ObjectId defined.");
        } else {
            objectIdField = objectIdFields.get(0);
        }

        return objectIdField;
    }

    private Field[] getDeclaredFieldsAsArray() {
        return persistedClass.getDeclaredFields();
    }

    private boolean checkIfDirty(Integer objectId, Object object) {
        boolean dirty = false;
        try {
            Object persistedObject = getObjectById(objectId);
            Field[] fields = getDeclaredFieldsAsArray();
            for (Field field : fields) {
                boolean accessibilityPersisted = field.canAccess(persistedObject);
                field.setAccessible(true);
                Object persistedValue = field.get(persistedObject);
                Object valueToBeChecked = field.get(object);

                // TODO add cascade if non primitive type or collection
                if (!persistedValue.equals(valueToBeChecked)) {
                    dirty = true;
                }
                field.setAccessible(accessibilityPersisted);
                if (dirty) {
                    return true;
                }
            }
            return false;
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
    }

}
