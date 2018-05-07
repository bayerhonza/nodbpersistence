package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;
import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.core.helpers.ConvertStringToType;
import cz.vutbr.fit.nodbpersistence.core.helpers.XmlException;
import cz.vutbr.fit.nodbpersistence.core.helpers.XmlHelper;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.core.storage.XMLParseException;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
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
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main persistence unit for all instances of a class. It collects all the objects and performs all the
 * actions with persisted object: persist, update or load
 *
 */
public class DefaultClassManagerImpl extends AbstractClassManager{
    private final ObjectInstantiator objectInstantiator;

    private final Field objectIdField;

    /**
     * Constructor of ClassManager
     * @param persistenceContext persistence unit context
     * @param persistedClass    class to be persisted by this classmanager
     * @param xmlFileExists     flag of already existing persistence system.
     * @param classFileHandler handler of file for class
     */
    public DefaultClassManagerImpl(PersistenceContext persistenceContext, Class<?> persistedClass, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        super(persistenceContext,xmlFileExists,persistedClass,classFileHandler,PersistenceContext.XML_ELEMENT_ROOT);
        this.objectIdField = getObjectIdField();
        this.objectInstantiator = new ObjenesisStd().getInstantiatorOf(persistedClass);
        if (!xmlFileExists) {
            initXMLDocument(persistedClass,PersistenceContext.XML_ELEMENT_ROOT);
        }
    }

    @Override
    void refreshPersistedObjects() {
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

    @Override
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

    @Override
    public Object performLoad(Long objectId) {
        return getObjectById(objectId);
    }

    @Override
    public void performPersist(PersistEntityEvent persistEvent) throws PersistenceException {
        Object persistedObject = persistEvent.getObject();
        Long objectId = getObjectId(persistedObject);
        if (isAlreadyPersisted(objectId)) {
            updateObject(objectId, persistedObject);
        } else {
            persistObject(objectId, persistedObject, persistEvent.getSource());
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
        //System.out.println("persisting " + object.getClass().getName() + "#" + objectId);
        try {
            // top level XML element <object>
            Element persistedObjectElement = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_OBJECT);
            rootElement.appendChild(persistedObjectElement);
            objectsInProgress.add(objectId);

            // attribute of top level XML element with objectId
            persistedObjectElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, objectId.toString());

            Field[] fields = object.getClass().getDeclaredFields();
            Class<?> inheritedClass = object.getClass().getSuperclass();

            // create inherited class element
            if (!inheritedClass.equals(Object.class)) {
                // inheritance
                Element inheritedXmlElement = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_INHERITED);
                persistedObjectElement.appendChild(inheritedXmlElement);
                createInheritedClassXml(inheritedClass, inheritedXmlElement, object, persistenceManager);

            }
            createFieldsXML(fields, persistedObjectElement, object, persistenceManager);

            objectsInProgress.remove(objectId);
            registerObject(object,objectId);
            persistedObjects.put(objectId, persistedObjectElement);
            flushXMLDocument();
        } catch (IllegalAccessException | FileNotFoundException | TransformerException e) {
            throw new PersistenceException(e);
        }
    }

    private void createInheritedClassXml(Class<?> inheritedClass,
                                         Element rootElement,
                                         Object object,
                                         PersistenceManager persistenceManager) throws IllegalAccessException {

        // get superclass fields
        Element inheritedClassXmlElement = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_INHERITED_CLASS);
        rootElement.appendChild(inheritedClassXmlElement);
        if (!inheritedClass.getSuperclass().equals(Object.class)) {
            createInheritedClassXml(inheritedClass.getSuperclass(), inheritedClassXmlElement, object, persistenceManager);
        }
        // write superclass name
        inheritedClassXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_NAME, inheritedClass.getName());
        // create XML elements for superclass fields
        Field[] inheritedClassFields = inheritedClass.getDeclaredFields();
        createFieldsXML(inheritedClassFields, inheritedClassXmlElement, object, persistenceManager);
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

    @Override
    public Long getObjectId(Object object) throws PersistenceException {
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

    private Node getObjectAttributeByName(Long objectId, String name) {
        // XPath query "/
        String query = "/" + PersistenceContext.XML_ELEMENT_ROOT + "/"
                + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]/"
                + PersistenceContext.XML_ATTRIBUTE_FIELD + "[@" + PersistenceContext.XML_ATTRIBUTE_FIELD_NAME + "=\"" + name + "\"]";
        System.out.println(query);
        return queryXMLModel(query);
    }

    @Override
    public Object getObjectById(Long objectId) {
        Element objectElement = getObjectNodeById(objectId);

        Object newObj = objectInstantiator.newInstance();
        persistenceContext.registerTempReference(ClassHelper.createReferenceString(newObj, objectId), newObj);

        // setting of objectID
        try {
            setObjectId(newObj, objectElement);

            // NodeList of <inherited> elements
            NodeList inheritedNodeList = objectElement.getElementsByTagName(PersistenceContext.XML_ELEMENT_INHERITED);
            // nodelist should contain just 1 <inherited> element.
            if (inheritedNodeList.getLength() != 0) {
                if (inheritedNodeList.getLength() != 1) {
                    throw new PersistenceException("Inheritance error.");
                }
                // get nodelist of
                Node inheritedClassXmlField = inheritedNodeList.item(0);
                Class<?> inheritedClass = persistedClass.getSuperclass();
                if (!inheritedClass.equals(Object.class)) {
                    Element firstSuperClassXmlElement = XmlHelper.getChildByNameAndAttribute(inheritedClassXmlField, PersistenceContext.XML_ATTRIBUTE_INHERITED_CLASS, PersistenceContext.XML_ATTRIBUTE_FIELD_NAME, inheritedClass.getName());
                    setInheritedFields(inheritedClass, newObj, firstSuperClassXmlElement);
                }
            }

            Field[] classFields = persistedClass.getDeclaredFields();
            setFieldsValue(objectElement, classFields, newObj);

            return newObj;
        } catch (XmlException e) {
            throw new PersistenceException(e);
        }
    }

    private void setObjectId(Object newObj, Element objectXmlElement) {
        try {
            boolean accessibilityId = objectIdField.canAccess(newObj);
            objectIdField.setAccessible(true);
            Type typeId = objectIdField.getType();
            String idToString = objectXmlElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID);
            objectIdField.set(newObj, ConvertStringToType.convertStringToType(typeId, idToString));
            objectIdField.setAccessible(accessibilityId);
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
    }

    private void setInheritedFields(Class<?> inheritedClass, Object newObj, Element classXmlElement) throws XmlException {
        Class<?> nextSuperclass = inheritedClass.getSuperclass();
        if (!nextSuperclass.equals(Object.class)) {
            Element nextSuperClassXmlElement = XmlHelper.getChildByNameAndAttribute(classXmlElement, PersistenceContext.XML_ATTRIBUTE_INHERITED_CLASS, PersistenceContext.XML_ATTRIBUTE_FIELD_NAME, nextSuperclass.getName());
            setInheritedFields(nextSuperclass, newObj, nextSuperClassXmlElement);
        }
        Field[] inheritedFields = inheritedClass.getDeclaredFields();
        setFieldsValue(classXmlElement, inheritedFields, newObj);
    }

    private void setFieldsValue(Element parent, Field[] classFields, Object newObject) {
        try {
            for (Field field : classFields) {

                if (field.isAnnotationPresent(ObjectId.class)) {
                    continue;
                }
                Element fieldXmlElement = XmlHelper.getChildByNameAndAttribute(parent, PersistenceContext.XML_ATTRIBUTE_FIELD, PersistenceContext.XML_ATTRIBUTE_FIELD_NAME, field.getName());
                boolean accessibility = field.canAccess(newObject);
                field.setAccessible(true);

                // TODO support collections and non primitive types
                if (fieldXmlElement.hasAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL)) {
                    field.set(newObject, null);
                } else if(field.getType().isEnum()) {
                    String fieldValue = fieldXmlElement.getTextContent();
                    Object[] enumConstants = field.getType().getEnumConstants();
                    boolean found = false;
                    for (Object enumValue : enumConstants) {
                        if (fieldValue.equals(enumValue.toString())) {
                            field.set(newObject,enumValue);
                            found = true;
                            break;

                        }
                    }
                    if (!found) {
                        throw new PersistenceException("Unknown value " + fieldValue + "of enum " + field.getType().getName());
                    }
                } else if (field.getType().isArray()) {
                    Object array = loadArray(fieldXmlElement,field.getType().getComponentType());
                    field.set(newObject,array);
                } else if (ClassHelper.isSimpleValueType(field.getType())) {
                    String fieldValue = fieldXmlElement.getTextContent();
                    field.set(newObject, ConvertStringToType.convertStringToType(field.getType(), fieldValue));
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    Collection newCollection = (Collection) loadCollection(fieldXmlElement);
                    field.set(newObject, newCollection);
                } else if(Map.class.isAssignableFrom(field.getType())) {
                    Map newMap = (Map) loadMap(fieldXmlElement,field.getType());
                    field.set(newObject,newMap);
                }else {
                    // cascade

                    if (!fieldXmlElement.hasAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE)) {
                        throw new PersistenceException("Bad XML. " + PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE + " expected.");
                    }
                    Object cascadeObject = getObjectByReference(fieldXmlElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE));
                    field.set(newObject, cascadeObject);
                }
                field.setAccessible(accessibility);

            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object loadMap(Element node, Class<?> mapType) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> collectionClass = Class.forName(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS));
        Constructor collectionConstructor = collectionClass.getConstructor();
        Map newMap = (Map) collectionConstructor.newInstance();

        NodeList entries = node.getChildNodes();
        for (int i = 0; i < entries.getLength(); i++) {
            if (entries.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element keyElement = (Element) entries.item(i).getFirstChild();
            Element valueElement = XmlHelper.getNextElement(keyElement);
            if (valueElement == null) {
                throw new PersistenceException("No value element for map entry.");
            }
            Object keyObject = loadObjectFromElement(keyElement);
            Object valueObject = loadObjectFromElement(valueElement);
            newMap.put(keyObject,valueObject);
        }

        return newMap;
    }

    private Object loadArray(Element node, Class<?> arrayType) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Integer arraySize = Integer.valueOf(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_ARRAY_SIZE));
        Object newArray = Array.newInstance(arrayType,arraySize);
        NodeList items = node.getChildNodes();
        Integer arrayIndex = 0;
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) items.item(i);
            Object result = loadObjectFromElement(element);
            Array.set(newArray, arrayIndex++, result);
        }
        return newArray;

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
            Object resultObject = loadObjectFromElement(element);
            newCollection.add(resultObject);
        }
        return newCollection;
    }

    private Object loadObjectFromElement(Element element) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (element.hasAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL)) {
            return null;
        }
        if (element.hasAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE)) {
            return getObjectByReference(element.getAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE));
        }

        Class<?> instClass = Class.forName(element.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS));
        if (ClassHelper.isSimpleValueType(instClass)) {
            String fieldValue = element.getTextContent();
           return ConvertStringToType.convertStringToType(instClass, fieldValue);
        } else if (Collection.class.isAssignableFrom(instClass)) {
           return loadCollection(element);
        } else if (instClass.isArray()) {
            return loadArray(element,instClass.getComponentType());
        } else if (Map.class.isAssignableFrom(instClass)) {
            return loadMap(element,instClass);
        } else {
            return null;
        }
    }



    private Field getObjectIdField() {
        List<Field> objectIdFields = Arrays.stream(persistedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ObjectId.class))
                .collect(Collectors.toList());
        Field objectIdField;

        if (objectIdFields.size() > 1) {
            throw new PersistenceException("Multiple ObjectId defined.");
        } else if (objectIdFields.size() == 0) {
            throw new PersistenceException("No ObjectId defined in class " + persistedClass.getName() + ".");
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



}
