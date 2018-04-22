package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.annotations.ObjectId;
import cz.fit.persistence.core.PersistenceContext;
import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.helpers.ClassHelper;
import cz.fit.persistence.core.helpers.ConvertStringToType;
import cz.fit.persistence.core.storage.ClassFileHandler;
import cz.fit.persistence.core.storage.XMLParseException;
import cz.fit.persistence.exceptions.PersistenceException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;
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
    private final LinkedList<List<Field>> inheratedClasses;

    private final Field objectIdField;


    private HashMap<Long, Node> persistedObjects = new HashMap<>();
    private Set<Long> objectsInProgress = new HashSet<>();
    private IdGenerator idGenerator;


    private ClassFileHandler fileHandler;

    // XML model
    private DocumentBuilder documentBuilder;
    private Document xmlDocument;
    private Element rootElement;
    private Transformer transformer;
    private XPathFactory xPathFactory = XPathFactory.newInstance();


    public DefaultClassManagerImpl(PersistenceContext persistenceContext, Class<T> persistedClass, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        this.persistenceContext = persistenceContext;
        this.persistedClass = persistedClass;
        this.fileHandler = classFileHandler;
        this.objectIdField = getObjectIdField();

        this.inheratedClasses = getInheritedClassAndFields(persistedClass);
        if (!xmlFileExists) {
            initXMLDocument(persistedClass);
            initXMLTransformer();
        } else {
            refreshPersistedObjects();
        }
    }

    private void refreshPersistedObjects() {
        initXMLDocumentBuilder();
        initXMLTransformer();

        try {
            xmlDocument = documentBuilder.parse(fileHandler.getXmlClassFile());
            Element rootElementLocal = xmlDocument.getDocumentElement();
            if (rootElementLocal.getNodeName().equals(PersistenceContext.XML_ELEMENT_ROOT)) {
                rootElement = rootElementLocal;
                if (rootElement.hasAttribute(PersistenceContext.XML_ELEMENT_ID_GENERATOR)) {
                    Attr idGenAttr = rootElementLocal.getAttributeNode(PersistenceContext.XML_ELEMENT_ID_GENERATOR);
                    idGenerator = new IdGenerator(idGenAttr, Integer.parseInt(idGenAttr.getValue()));
                } else {
                    throw new PersistenceException("Id generator value is missing");
                }
            } else {
                throw new XMLParseException();
            }
            NodeList objectNodes = rootElement.getElementsByTagName(PersistenceContext.XML_ELEMENT_OBJECT);
            for (int i = 0; i < objectNodes.getLength(); i++) {
                Node node = objectNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Long objectId = Long.parseLong(((Element) node).getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID));
                    persistedObjects.put(objectId, node);
                }
            }
            normalizeXMLModel();


        } catch (SAXException | IOException | XMLParseException e) {
            throw new PersistenceException(e);
        }
    }

    private LinkedList<List<Field>> getInheritedClassAndFields(Class<?> baseClass) {
        LinkedList<List<Field>> linkedList = new LinkedList<>();
        Class<?> inheritedClass = persistedClass.getSuperclass();
        if (inheritedClass.equals(Object.class)) {
            return null;
        } else {
            // inheritance
            while (!inheritedClass.equals(Object.class)) {
                linkedList.add(Arrays.asList(inheritedClass.getDeclaredFields()));
                inheritedClass = inheritedClass.getSuperclass();
            }
        }
        return linkedList;
    }


    public Long isPersistentOrInProgress(Object object) {
        Long objectId = getObjectId(object);
        if (isAlreadyPersisted(objectId)) {
            if (checkIfDirty(objectId, object)) {
                updateObject(objectId, object);
            }
            return objectId;
        } else if (objectsInProgress.contains(objectId)) {
            return objectId;
        } else {
            return null;
        }
    }

    public Object performLoad(Long objectId) {
        return getObjectById(objectId);
    }

    public void performPersist(PersistEntityEvent persistEvent) throws PersistenceException {
        Object persistedObject = persistEvent.getObject();
        Long objectId = getObjectId(persistedObject);
        if (isAlreadyPersisted(objectId)) {
            updateObject(objectId, persistedObject);
        } else {
            persistObject(objectId, persistedObject, persistEvent.getSource());
        }
    }

    private Node queryXMLModel(String xmlPathQuery) {
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

    /**
     * Delete whitespace nodes from XML.
     */
    private void normalizeXMLModel() {
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                rootElement.removeChild(node);
            }
        }
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
        rootElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_CLASS, persistedClass.getCanonicalName());
        Attr idGenAttr = xmlDocument.createAttribute(PersistenceContext.XML_ELEMENT_ID_GENERATOR);
        idGenerator = new IdGenerator(idGenAttr);
        rootElement.setAttributeNode(idGenAttr);
        xmlDocument.appendChild(rootElement);
    }

    private void initXMLTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            throw new PersistenceException(e);
        }

    }

    private void updateObject(Long objectId, Object object) {
        boolean isDirty = checkIfDirty(objectId, object);
        if (isDirty) {
            Field[] fields = persistedClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ObjectId.class)) {
                    continue;
                }
                Node node = getObjectAttributeByName(objectId, field.getName());
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

    private void persistObject(Long objectId, Object object, PersistenceManager persistenceManager) throws PersistenceException {
        //System.out.println("persisting " + object.getClass().getCanonicalName() + "#" + objectId);
        try {
            // top level XML element <object>
            Element persistedObjectElement = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_OBJECT);
            rootElement.appendChild(persistedObjectElement);
            objectsInProgress.add(objectId);

            // attribute of top level XML element with objectId
            persistedObjectElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, objectId.toString());

            Field[] fields = object.getClass().getDeclaredFields();
            Class<?> inheritedClass = object.getClass().getSuperclass();

            if (inheritedClass != null) {
                // inheritance
                Element inheritedXmlElement = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_INHERITED);
                persistedObjectElement.appendChild(inheritedXmlElement);
                while (!inheritedClass.equals(Object.class)) {
                    // get superclass fields
                    Field[] inheritedClassFields = inheritedClass.getDeclaredFields();
                    Element inheritedClassXmlElement = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_INHERITED_CLASS);
                    inheritedXmlElement.appendChild(inheritedClassXmlElement);
                    // write superclass name
                    inheritedClassXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_NAME, inheritedClass.getCanonicalName());
                    // create XML elements for superclass fields
                    createFieldsXML(inheritedClassFields, inheritedClassXmlElement, object, persistenceManager);
                    inheritedClass = inheritedClass.getSuperclass();
                }
            }
            createFieldsXML(fields, persistedObjectElement, object, persistenceManager);

            objectsInProgress.remove(objectId);
            persistedObjects.put(objectId, persistedObjectElement);
            flushXMLDocument();
        } catch (IllegalAccessException | FileNotFoundException | TransformerException e) {
            throw new PersistenceException(e);
        }
    }

    private void createFieldsXML(Field[] fields,
                                 Element objectXmlElement,
                                 Object object,
                                 PersistenceManager persistenceManager) throws IllegalAccessException {
        for (Field field : fields) {
            if (field.isAnnotationPresent(ObjectId.class)) {
                continue;
            }

            // XML element for field of class, format <field name="nameOfField">value</field>
            Element fieldXmlElement = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_FIELD);
            objectXmlElement.appendChild(fieldXmlElement);
            fieldXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_NAME, field.getName());


            boolean accessible = field.canAccess(object);
            field.setAccessible(true);

            // check the type of the field
            Object fieldValue = field.get(object);
            createXMLStructure(fieldXmlElement, fieldValue, persistenceManager);


            field.setAccessible(accessible);
        }
    }

    private void flushXMLDocument() throws TransformerException, FileNotFoundException {
        DOMSource source = new DOMSource(xmlDocument);
        StreamResult result = new StreamResult(fileHandler.getXMLOutputStream());
        transformer.transform(source, result);
    }

    private Long getObjectId(Object object) throws PersistenceException {
        if (!object.getClass().equals(persistedClass)) {
            throw new PersistenceException("Wrong Class Manager.");
        }
        try {
            boolean accessibility = objectIdField.canAccess(object);
            objectIdField.setAccessible(true);
            Class<?> fieldType = objectIdField.getType();
            Long objectIdValue;

            // TODO support more type of objectId

            if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                objectIdValue = (Long) objectIdField.get(object);
            } else {
                throw new PersistenceException("Object ID type not supported in " + object.getClass().getName()
                        + ":" + objectIdField.getName() + ".");
            }
            if (objectIdValue == null || objectIdValue == 0) {
                Long idNext = idGenerator.getNextId();
                objectIdField.set(object, idNext);
                objectIdValue = idNext;
            }
            objectIdField.setAccessible(accessibility);
            return objectIdValue;
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
    }

    public Node getObjectNodeById(Long objectId) {
        return queryXMLModel("/" + PersistenceContext.XML_ELEMENT_ROOT + "/" + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]");

    }

    private Node getObjectAttributeByName(Long objectId, String name) {
        String query = "/" + PersistenceContext.XML_ELEMENT_ROOT + "/"
                + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]/"
                + PersistenceContext.XML_ATTRIBUTE_FIELD + "[@" + PersistenceContext.XML_ATTRIBUTE_FIELD_NAME + "=\"" + name + "\"]";
        System.out.println(query);
        return queryXMLModel(query);
    }

    private Object getObjectById(Long objectId) {
        Node objectNode = getObjectNodeById(objectId);
        if (objectNode == null) {
            throw new PersistenceException("Object with ID not found.");
        }
        Object newObj = ClassHelper.instantiateClass(persistedClass);
        persistenceContext.registerTempReference(ClassHelper.createReferenceString(newObj, objectId), newObj);

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
            Element xmlElementField = (Element) xmlField;
            String fieldName = xmlField.getAttributes().getNamedItem(PersistenceContext.XML_ATTRIBUTE_FIELD_NAME).getNodeValue();
            try {
                Field field = persistedClass.getDeclaredField(fieldName);
                boolean accessibility = field.canAccess(newObj);
                field.setAccessible(true);

                // TODO support collections and non primitive types
                setFieldsValues(xmlElementField,field,newObj);
                field.setAccessible(accessibility);

            } catch (Exception e) {
                throw new PersistenceException(e);
            }


        }
        return newObj;
    }

    private void setFieldsValues(Element fieldXmlElement, Field field, Object newObject) {
        try {
            if (fieldXmlElement.hasAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL)) {
                field.set(newObject, null);
            } else if (ClassHelper.isSimpleValueType(field.getType())) {
                String fieldValue = fieldXmlElement.getTextContent();
                field.set(newObject, ConvertStringToType.convertStringToType(field.getType(), fieldValue));
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                Collection newCollection = (Collection) loadCollection(fieldXmlElement);
                field.set(newObject, newCollection);
            } else {
                // cascade

                if (!fieldXmlElement.hasAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE)) {
                    throw new PersistenceException("Bad XML. " + PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE + " expected.");
                }
                Object cascadeObject = getObjectByReference(fieldXmlElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE));
                field.set(newObject, cascadeObject);
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    private Object getObjectByReference(String reference) throws ClassNotFoundException {
        if (persistenceContext.isReferenceRegistered(reference)) {
            return persistenceContext.getObjectByReference(reference);
        }
        String[] parsedReference = reference.split("#");
        String className = parsedReference[0];
        Long cascadeObjectId = Long.parseLong(parsedReference[1]);
        Class<?> referencedClass = Class.forName(className);
        if (referencedClass.equals(persistedClass)) {
            return getObjectById(cascadeObjectId);
        } else {
            DefaultClassManagerImpl cascadeObjectManager = persistenceContext.findClassManager(referencedClass);
            return cascadeObjectManager.getObjectById(cascadeObjectId);
        }
    }

    @SuppressWarnings("unchecked")
    private Object loadCollection(Element node) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> collectionClass = Class.forName(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS));
        Constructor collectionConstructor = collectionClass.getConstructor();
        Collection newCollection = (Collection) collectionConstructor.newInstance();
        NodeList items = node.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) items.item(i);
            if (element.hasAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE)) {
                Object cascadeObject = getObjectByReference(element.getAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE));
                newCollection.add(cascadeObject);
                continue;
            }

            Class<?> instClass = Class.forName(element.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS));
            if (ClassHelper.isSimpleValueType(instClass)) {
                String fieldValue = element.getTextContent();
                newCollection.add(ConvertStringToType.convertStringToType(instClass, fieldValue));
            } else if (Collection.class.isAssignableFrom(instClass)) {
                newCollection.add(loadCollection(element));
            }

        }
        return newCollection;
    }

    /**
     * Creates an XML in-memory model of object.
     *
     * @param xmlField           root field for XML
     * @param object             object to be stored
     * @param persistenceManager persistence manager of event to store the object if part of collection
     */
    private void createXMLStructure(Element xmlField, Object object, PersistenceManager persistenceManager) {
        if (object == null) {
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL, Boolean.TRUE.toString());
        } else if (ClassHelper.isSimpleValueType(object.getClass())) {
            xmlField.appendChild(xmlDocument.createTextNode(object.toString()));
        } else if (object instanceof Collection<?>) {
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, object.getClass().getCanonicalName());

            // if object is a collection, then recast
            Collection fieldValueCollection = (Collection) object;
            for (Object o : fieldValueCollection) {
                Element xmlItemElement = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_COLLECITON_ITEM);
                if (o == null) {
                    xmlItemElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL, Boolean.TRUE.toString());
                    xmlField.appendChild(xmlItemElement);
                    continue;
                }
                xmlField.appendChild(xmlItemElement);

                if (ClassHelper.isSimpleValueType(o.getClass())) {
                    xmlItemElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, o.getClass().getCanonicalName());
                    xmlItemElement.appendChild(xmlDocument.createTextNode(o.toString()));
                } else if (o instanceof Collection<?>) {
                    // if item of collection is a collection, recurse
                    xmlItemElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, o.getClass().getCanonicalName());
                    createXMLStructure(xmlItemElement, o, persistenceManager);
                } else {
                    // persist item as object
                    String reference = startCascade(o, persistenceManager);
                    xmlItemElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, reference);
                }
            }
        } else if (object instanceof Map) {

        } else {
            String reference = startCascade(object, persistenceManager);
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, reference);

        }

    }

    private boolean isAlreadyPersisted(Long objectId) {
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
            throw new PersistenceException("No ObjectId defined in class " + persistedClass.getCanonicalName() + ".");
        } else {
            objectIdField = objectIdFields.get(0);
        }

        return objectIdField;
    }

    private Field[] getDeclaredFieldsAsArray() {
        return persistedClass.getDeclaredFields();
    }

    private boolean checkIfDirty(Long objectId, Object object) {
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
        String reference = persistenceContext.getReferenceIfPersisted(object);
        if (reference == null) {
            persistenceManager.persist(object);
            Long objectId = persistenceContext.findClassManager(object.getClass()).getObjectId(object);
            return object.getClass().getCanonicalName() + "#" + objectId;
        } else {
            return reference;
        }
    }
}
