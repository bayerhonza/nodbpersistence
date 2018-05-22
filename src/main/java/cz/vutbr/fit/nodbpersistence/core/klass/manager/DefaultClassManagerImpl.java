package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;
import cz.vutbr.fit.nodbpersistence.annotations.Transient;
import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.core.helpers.ConvertStringToType;
import cz.vutbr.fit.nodbpersistence.core.helpers.XmlException;
import cz.vutbr.fit.nodbpersistence.core.helpers.XmlHelper;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.core.storage.XMLParseException;
import cz.vutbr.fit.nodbpersistence.exceptions.NoObjectIdFoundException;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class manager for all user defined classes.
 */
public class DefaultClassManagerImpl extends AbstractClassManager {

    public static final String XML_ELEMENT_ROOT = "class";
    public static final String XML_ELEMENT_OBJECT = "object";
    public static final String XML_ATTRIBUTE_FIELD = "field";

    public static final String XML_ELEMENT_INHERITED = "inherited";
    public static final String XML_ATTRIBUTE_INHERITED_CLASS = "inherClass";

    public static final String XML_ELEMENT_STATIC_PART = "static";

    private Constructor noArgConstructor;
    private Element staticXmlElement;
    private HashMap<Field, Element> staticElements = new HashMap<>();

    private final Field objectIdField;

    /**
     * Constructor of ClassManager
     *
     * @param persistenceContext persistence unit context
     * @param persistedClass     class to be persisted by this classmanager
     * @param xmlFileExists      flag of already existing persistence system.
     * @param classFileHandler   handler of file for class
     */
    public DefaultClassManagerImpl(PersistenceContext persistenceContext, Class<?> persistedClass, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler);
        this.objectIdField = getObjectIdField();
        this.noArgConstructor = getNoArgConstructor();
        if (!xmlFileExists) {
            createXmlStaticElement();
        } else {
            registerStaticXmlElement();
        }

    }

    @Override
    public String getRootXmlElementName() {
        return XML_ELEMENT_ROOT;
    }

    @Override
    public String getItemXmlElementName() {
        return XML_ELEMENT_OBJECT;
    }

    @Override
    public void persistObject(Object object, PersistenceManager persistenceManager) {
        try {
            persistenceManager.addModifiedClassManager(this);

            Long objectId = getObjectId(object);
            registerPersistedObject(object, objectId);
            // top level XML element <object>
            Element persistedObjectElement = xmlDocument.createElement(XML_ELEMENT_OBJECT);
            rootElement.appendChild(persistedObjectElement);
            //objectsInProgress.add(objectId);

            // attribute of top level XML element with objectId
            persistedObjectElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, objectId.toString());

            Field[] fields = object.getClass().getDeclaredFields();
            Class<?> inheritedClass = object.getClass().getSuperclass();

            // create inherited class element
            if (!inheritedClass.equals(Object.class)) {
                // inheritance
                Element inheritedXmlElement = xmlDocument.createElement(XML_ELEMENT_INHERITED);
                persistedObjectElement.appendChild(inheritedXmlElement);
                createInheritedClassXml(inheritedClass, inheritedXmlElement, object, persistenceManager);

            }
            createFieldsXML(fields, persistedObjectElement, object, persistenceManager);
            assignIdToElement(objectId, persistedObjectElement);
        } catch (ReflectiveOperationException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Long performPersist(PersistEntityEvent persistEvent) {
        try {
            Object persistedObject = persistEvent.getObject();
            Long objectId = getObjectId(persistedObject);
            if (isAlreadyPersisted(objectId)) {
                updateObject(objectId, persistedObject);
            } else {
                persistObject(persistedObject, persistEvent.getSource());
            }
            return objectId;
        } catch (ReflectiveOperationException e) {
            throw new PersistenceException(e);
        }
    }

    public void loadStaticFields() {
        try {
            NodeList nodeList = rootElement.getElementsByTagName(XML_ELEMENT_STATIC_PART);
            if (nodeList.getLength() > 1) {
                throw new XMLParseException("Multiple static parts defined.");
            }
            if (nodeList.getLength() == 0) {
                return;
            }
            if (nodeList.item(0).getNodeType() != Node.ELEMENT_NODE) {
                throw new XMLParseException("Bad XML syntax.");
            }
            Element staticElement = (Element) nodeList.item(0);
            NodeList staticFieldNodeList = staticElement.getElementsByTagName(XML_ATTRIBUTE_FIELD);
            for (int i = 0; i < staticFieldNodeList.getLength(); i++) {
                Node node = staticFieldNodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Object staticValue = loadObjectFromElement(element);
                    String staticFieldName = element.getAttribute(PersistenceContext.XML_ATTRIBUTE_NAME);
                    Field staticField = persistedClass.getDeclaredField(staticFieldName);
                    ClassHelper.setFieldValue(staticField, null, staticValue);
                }
            }

        } catch (ReflectiveOperationException | XMLParseException e) {
            throw new PersistenceException(e);
        }
    }

    private Constructor getNoArgConstructor() {
        try {
            return persistedClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new PersistenceException("No public constructor found for class " + persistedClass.getCanonicalName() + ".", e);
        }
    }

    private void createXmlStaticElement() {
        Field[] declaredFields = persistedClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                staticXmlElement = xmlDocument.createElement(XML_ELEMENT_STATIC_PART);
                rootElement.appendChild(staticXmlElement);
                return;
            }
        }
    }

    private void registerStaticXmlElement() {
        NodeList nodeList = xmlDocument.getElementsByTagName(XML_ELEMENT_STATIC_PART);
        if (nodeList.getLength() > 1) {
            throw new PersistenceException("XML parse error of static fiedls.");
        }
        if (nodeList.getLength() == 0) {
            return;
        }
        Node node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            staticXmlElement = (Element) node;
        }

        NodeList fieldNodeList = staticXmlElement.getElementsByTagName(XML_ATTRIBUTE_FIELD);
        if (fieldNodeList.getLength() == 0) {
            return;
        }

        for (int i = 0; i < fieldNodeList.getLength(); i++) {
            Node fieldNode = fieldNodeList.item(i);
            if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fieldElement = (Element) fieldNode;
                String fieldName = fieldElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_NAME);
                try {
                    Field staticField = persistedClass.getDeclaredField(fieldName);
                    staticElements.put(staticField, fieldElement);
                } catch (NoSuchFieldException e) {
                    throw new PersistenceException(e);
                }
            }
        }

    }

    private void addOrEditStatic(Field staticField, PersistenceManager persistenceManager) throws ReflectiveOperationException {
        Element staticFieldXml;
        if (!staticElements.containsKey(staticField)) {
            staticFieldXml = xmlDocument.createElement(XML_ATTRIBUTE_FIELD);
            staticXmlElement.appendChild(staticFieldXml);
            staticFieldXml.setAttribute(PersistenceContext.XML_ATTRIBUTE_NAME, staticField.getName());
            staticElements.put(staticField, staticFieldXml);
        } else {
            staticFieldXml = staticElements.get(staticField);
        }

        Object fieldValue = ClassHelper.getFieldValue(staticField, null);
        createXMLStructure(staticFieldXml, fieldValue, persistenceManager);
    }


    private void updateObject(Long objectId, Object object) {
        // TODO updateObject
    }


    private void createInheritedClassXml(Class<?> inheritedClass,
                                         Element rootElement,
                                         Object object,
                                         PersistenceManager persistenceManager) throws ReflectiveOperationException {

        // get superclass fields
        Element inheritedClassXmlElement = xmlDocument.createElement(XML_ATTRIBUTE_INHERITED_CLASS);
        rootElement.appendChild(inheritedClassXmlElement);
        if (!inheritedClass.getSuperclass().equals(Object.class)) {
            createInheritedClassXml(inheritedClass.getSuperclass(), inheritedClassXmlElement, object, persistenceManager);
        }
        // write superclass name
        inheritedClassXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_NAME, inheritedClass.getName());
        // create XML elements for superclass fields
        Field[] inheritedClassFields = inheritedClass.getDeclaredFields();
        Field[] nonStatic = Arrays.stream(inheritedClassFields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
        createFieldsXML(nonStatic, inheritedClassXmlElement, object, persistenceManager);
    }

    private void createFieldsXML(Field[] fields,
                                 Element objectXmlElement,
                                 Object object,
                                 PersistenceManager persistenceManager) throws ReflectiveOperationException {
        for (Field field : fields) {
            if (field.isAnnotationPresent(ObjectId.class) || field.isAnnotationPresent(Transient.class)) {
                continue;
            }

            // XML element for field of class, format <field name="nameOfField">value</field>

            if (Modifier.isStatic(field.getModifiers())) {
                addOrEditStatic(field, persistenceManager);
            } else {
                Element fieldXmlElement = xmlDocument.createElement(XML_ATTRIBUTE_FIELD);
                objectXmlElement.appendChild(fieldXmlElement);
                fieldXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_NAME, field.getName());
                Object fieldValue = ClassHelper.getFieldValue(field, object);
                createXMLStructure(fieldXmlElement, fieldValue, persistenceManager);

            }


        }
    }

    private Long getObjectId(Object object) throws ReflectiveOperationException {
        Class<?> fieldType = objectIdField.getType();
        Long objectIdValue;

        // TODO support more type of objectId

        if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            objectIdValue = (Long) ClassHelper.getFieldValue(objectIdField, object);
        } else {
            throw new PersistenceException("Object ID type not supported in " + object.getClass().getName()
                    + ":" + objectIdField.getName() + ".");
        }
        if (objectIdValue == null || objectIdValue == 0) {
            Long idNext = idGenerator.getNextId();
            ClassHelper.setFieldValue(objectIdField, object, idNext);
            objectIdValue = idNext;
        }
        return objectIdValue;
    }

    private Node getObjectAttributeByName(Long objectId, String name) {
        // XPath query "/
        String query = "/" + XML_ELEMENT_ROOT + "/"
                + XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]/"
                + XML_ATTRIBUTE_FIELD + "[@" + PersistenceContext.XML_ATTRIBUTE_NAME + "=\"" + name + "\"]";
        return queryXMLModel(query);
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

    @Override
    public Object getObjectById(Long objectId) {
        if (loadCache.containsKey(objectId)) {
            return loadCache.get(objectId);
        }
        Element objectElement = getObjectNodeById(objectId);
        return loadObject(objectElement);
    }

    private Object loadObject(Element objectElement) {
        Object newObj;
        try {
            newObj = noArgConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new PersistenceException(e);
        }
        Long objectId = Long.valueOf(objectElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID));
        registerLoadedObject(newObj, objectId);
        // setting of objectID
        try {
            setObjectId(newObj, objectElement);

            // NodeList of <inherited> elements
            NodeList inheritedNodeList = objectElement.getElementsByTagName(XML_ELEMENT_INHERITED);
            // nodelist should contain just 1 <inherited> element.
            if (inheritedNodeList.getLength() != 0) {
                if (inheritedNodeList.getLength() != 1) {
                    throw new PersistenceException("Inheritance error.");
                }
                // get nodelist of
                Node inheritedClassXmlField = inheritedNodeList.item(0);
                Class<?> inheritedClass = persistedClass.getSuperclass();
                if (!inheritedClass.equals(Object.class)) {
                    Element firstSuperClassXmlElement = XmlHelper.getChildByNameAndAttribute(inheritedClassXmlField, XML_ATTRIBUTE_INHERITED_CLASS, PersistenceContext.XML_ATTRIBUTE_NAME, inheritedClass.getName());
                    setInheritedFields(inheritedClass, newObj, firstSuperClassXmlElement);
                }
            }

            Field[] classFields = persistedClass.getDeclaredFields();
            setFieldsValue(objectElement, classFields, newObj);

            return newObj;
        } catch (XmlException | ReflectiveOperationException e) {
            throw new PersistenceException(e);
        }
    }

    private void setObjectId(Object newObj, Element objectXmlElement) throws ReflectiveOperationException {
        try {
            objectIdField.setAccessible(true);
            Class typeId = objectIdField.getType();
            String idToString = objectXmlElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID);
            ClassHelper.setFieldValue(objectIdField, newObj, ConvertStringToType.convertStringToType(typeId, idToString));
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
    }

    private void setInheritedFields(Class<?> inheritedClass, Object newObj, Element classXmlElement) throws XmlException {
        Class<?> nextSuperclass = inheritedClass.getSuperclass();
        if (!nextSuperclass.equals(Object.class)) {
            Element nextSuperClassXmlElement = XmlHelper.getChildByNameAndAttribute(classXmlElement, XML_ATTRIBUTE_INHERITED_CLASS, PersistenceContext.XML_ATTRIBUTE_NAME, nextSuperclass.getName());
            setInheritedFields(nextSuperclass, newObj, nextSuperClassXmlElement);
        }
        Field[] nonStatic = Arrays.stream(inheritedClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
        setFieldsValue(classXmlElement, nonStatic, newObj);
    }

    private void setFieldsValue(Element parent, Field[] classFields, Object newObject) {
        try {
            for (Field field : classFields) {

                if (field.isAnnotationPresent(ObjectId.class) || field.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Element fieldXmlElement = XmlHelper.getChildByNameAndAttribute(parent, XML_ATTRIBUTE_FIELD, PersistenceContext.XML_ATTRIBUTE_NAME, field.getName());
                boolean accessibility = field.isAccessible();
                field.setAccessible(true);

                if (fieldXmlElement.hasAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL)) {
                    field.set(newObject, null);
                } else if (ClassHelper.isSimpleValueType(field.getType())) {
                    String fieldValue = fieldXmlElement.getTextContent();
                    Class<?> fieldClass = Class.forName(fieldXmlElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS));
                    field.set(newObject, ConvertStringToType.convertStringToType(fieldClass, fieldValue));
                } else {
                    if (!fieldXmlElement.hasAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE)) {
                        throw new PersistenceException("Bad XML. " + PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE + " expected.");
                    }
                    String reference = fieldXmlElement.getAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE);
                    AbstractClassManager classManager;
                    if (field.getType().isArray()) {
                        classManager = persistenceContext.getArrayManager();
                    } else if (Collection.class.isAssignableFrom(field.getType())) {
                        classManager = persistenceContext.getCollectionManager();
                    } else if (Map.class.isAssignableFrom(field.getType())) {
                        classManager = persistenceContext.getMapManager();
                    } else {
                        classManager = persistenceContext.findClassManager(reference);
                    }
                    Object otherObject = classManager.getObjectByReference(reference);
                    field.set(newObject, otherObject);
                }
                field.setAccessible(accessibility);
            }
        } catch (ReflectiveOperationException | XmlException e) {
            throw new PersistenceException(e);
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
            throw new NoObjectIdFoundException("No ObjectId defined in class " + persistedClass.getName() + ".");
        } else {
            objectIdField = objectIdFields.get(0);
        }

        return objectIdField;
    }


}
