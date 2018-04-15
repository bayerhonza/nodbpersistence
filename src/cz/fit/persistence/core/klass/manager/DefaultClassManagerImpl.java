package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.PersistenceContext;
import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.helpers.ClassHelper;
import cz.fit.persistence.core.helpers.ConvertStringToType;
import cz.fit.persistence.core.helpers.HashHelper;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.core.storage.XMLParseException;
import cz.fit.persistence.exceptions.PersistenceException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main persistence unit for all instances of a class. It collects all the objects and performs all the
 * actions with persisted object: persist, update or load
 *
 * @param <T> class which class manager is responsible for
 */
public class DefaultClassManagerImpl<T> {
    private final PersistenceContext persistenceContext;

    private final Class<T> persistedClass;
    private final Integer classHashCode;

    private final Field objectIdField;


    private HashMap<Integer, Node> persistedObjects = new HashMap<>();
    private final IdGenerator idGenerator;


    private ClassFileHandler fileHandler;

    // XML model
    DocumentBuilder documentBuilder;
    private Document xmlDocument;
    private Element rootElement;
    private Transformer transformer;
    private XPathFactory xPathFactory = XPathFactory.newInstance();


    public DefaultClassManagerImpl(PersistenceContext persistenceContext, Class<T> persistedClass, Integer classHashCode, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        this.persistenceContext = persistenceContext;
        this.persistedClass = persistedClass;
        this.idGenerator = new IdGenerator();
        this.classHashCode = classHashCode;
        this.fileHandler = classFileHandler;
        this.objectIdField = getObjectIdField();

        if (!xmlFileExists) {
            initXMLDocument(persistedClass);
            initXMLTransformer();
        } else {
            refreshPersistedObjects();
        }
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

    public void refreshPersistedObjects() {
        initXMLDocumentBuilder();
        initXMLTransformer();

        try {
            xmlDocument = documentBuilder.parse(fileHandler.getXmlClassFile());
            xmlDocument.getDocumentElement().normalize();
            Element rootElementLocal = xmlDocument.getDocumentElement();
            if (rootElementLocal.getNodeName() == PersistenceContext.XML_ELEMENT_ROOT) {
                rootElement = rootElementLocal;
            } else {
                throw new XMLParseException();
            }
        } catch (SAXException | IOException | XMLParseException e) {
            throw new PersistenceException(e);
        }
    }

    public Object performLoad(Integer objectId) {
        return (T) getObjectById(objectId);
    }

    public void performPersist(PersistEntityEvent persistEvent) throws PersistenceException {
        Object persistedObject = persistEvent.getObject();
        Integer objectId = getObjectId(persistedObject);
        if (isAlreadyPersisted(objectId)) {
            updateObject(objectId, persistedObject);
        } else {
            persistObject(objectId, persistedObject, persistEvent.getSource());
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

    private void initXMLDocumentBuilder() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new PersistenceException(e);
        }
        xmlDocument = documentBuilder.newDocument();
    }

    private void initXMLDocument(Class<T> persistedClass) {
        initXMLDocumentBuilder();
        rootElement = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_ROOT);
        Attr className = xmlDocument.createAttribute(PersistenceContext.XML_ATTRIBUTE_CLASS);
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

    private void persistObject(Integer objectId, Object object, PersistenceManager persistenceManager) throws PersistenceException {

        try {
            // top level XML elemet <persistedObject>
            Element persistedObject = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_OBJECT);
            rootElement.appendChild(persistedObject);

            // attribute of top level XML element with objectId
            persistedObject.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, objectId.toString());

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(ObjectId.class)) {
                    continue;
                }

                // XML element for field of class, format <field name="nameOfField">value</field>
                Element xmlField = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_FIELD);
                persistedObject.appendChild(xmlField);
                xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_NAME, field.getName());


                boolean accessible = field.canAccess(object);
                field.setAccessible(true);

                // check the type of the field
                Object fieldValue = field.get(object);
                if (fieldValue == null || ClassHelper.isSimpleValueType(fieldValue.getClass())) {
                    xmlField.appendChild(xmlDocument.createTextNode(fieldValue == null ? null : fieldValue.toString()));
                } else if (fieldValue instanceof Collection<?>) {
                    xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLLECITON, Boolean.TRUE.toString());

                    Collection fieldValueCollection = (Collection) fieldValue;
                    for (Object o : fieldValueCollection) {
                        Element xmlItemElement = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_COLLECITON_ITEM);
                        xmlField.appendChild(xmlItemElement);

                        if (ClassHelper.isSimpleValueType(o.getClass())) {
                            xmlItemElement.appendChild(xmlDocument.createTextNode(o.toString()));
                        } else {
                            String reference = startCascade(o, persistenceManager);
                            xmlItemElement.appendChild(xmlDocument.createTextNode(reference));
                        }
                    }
                } else {
                    String reference = startCascade(fieldValue, persistenceManager);
                    xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, reference);

                }
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

    public Integer getObjectId(Object object) throws PersistenceException {
        if (!object.getClass().equals(persistedClass)) {
            throw new PersistenceException("Wrong Class Manager.");
        }
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
        return queryXMLModel("/" + PersistenceContext.XML_ELEMENT_ROOT + "/" + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]");

    }

    private Node getObjectAttributByName(Integer objectId, String name) {
        System.out.println("/" + PersistenceContext.XML_ELEMENT_ROOT + "/" + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]/" + name);
        return queryXMLModel("/" + PersistenceContext.XML_ELEMENT_ROOT + "/" + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]/" + name);
    }

    private Object getObjectById(Integer objectId) {
        Node objectNode = getObjectNodeById(objectId);

        // construction of object
        T newObj = null;
        try {
            Constructor<T> constructor = persistedClass.getConstructor();
            boolean accs = constructor.canAccess(null);
            constructor.setAccessible(true);
            newObj = constructor.newInstance();
            constructor.setAccessible(accs);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        // setting of objectID
        try {
            boolean accessibilityId = objectIdField.canAccess(newObj);
            objectIdField.setAccessible(true);
            Type typeId = objectIdField.getType();
            String idToString = objectNode.getAttributes().getNamedItem(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID).getNodeValue();
            objectIdField.set(newObj, ConvertStringToType.convertStringToType(typeId, idToString));
            objectIdField.setAccessible(accessibilityId);
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }

        // setting of all the other attributes
        NodeList fields = objectNode.getChildNodes();
        for (int i = 0; i < fields.getLength(); i++) {
            Node xmlField = fields.item(i);
            if (xmlField.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String fieldName = xmlField.getAttributes().getNamedItem("name").getNodeValue();
            try {
                Field field = persistedClass.getDeclaredField(fieldName);
                boolean accessibility = field.canAccess(newObj);
                field.setAccessible(true);

                // TODO support collections and non primitive types
                if (ClassHelper.isSimpleValueType(field.getClass())) {
                    String fieldValue = xmlField.getFirstChild().getNodeValue();
                    field.set(newObj, ConvertStringToType.convertStringToType(field.getType(), fieldValue));
                } else
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

    private String startCascade(Object object, PersistenceManager persistenceManager) {
        persistenceManager.persist(object);
        Integer objectId = persistenceContext.findClassManager(object.getClass()).getObjectId(object);

        Integer hashClass = HashHelper.getHashFromClass(object.getClass());
        System.out.println(hashClass + "#" + objectId);
        return hashClass + "#" + objectId;
    }

}
