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
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main persistence unit for all instances of a class. It collects all the objects and performs all the
 * actions with persisted object: persist, update or load
 */
public class DefaultClassManagerImpl extends AbstractClassManager {

    public static final String XML_ELEMENT_ROOT = "class";
    public static final String XML_ELEMENT_OBJECT = "object";
    public static final String XML_ATTRIBUTE_FIELD = "field";

    public static final String XML_ELEMENT_INHERITED = "inherited";
    public static final String XML_ATTRIBUTE_INHERITED_CLASS = "inherClass";

    public static final String XML_ATTRIBUTE_ARRAY_SIZE = "size";

    private final ObjectInstantiator objectInstantiator;

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
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler, XML_ELEMENT_ROOT);
        this.objectIdField = getObjectIdField();
        this.objectInstantiator = new ObjenesisStd().getInstantiatorOf(persistedClass);

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
    public void performPersist(PersistEntityEvent persistEvent) throws PersistenceException {
        Object persistedObject = persistEvent.getObject();
        Long objectId = getObjectId(persistedObject);
        if (isAlreadyPersisted(objectId)) {
            updateObject(objectId, persistedObject);
        } else {
            persistObject(persistedObject, persistEvent.getSource());
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

    @Override
    public void persistObject(Object object, PersistenceManager persistenceManager) {
        //System.out.println("persisting " + object.getClass().getName() + "#" + objectId);
        Long objectId = getObjectId(object);
        registerObject(object,objectId);
        try {
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

            //objectsInProgress.remove(objectId);
            registerObject(object, objectId);
            idToNode.put(objectId, persistedObjectElement);
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
        Element inheritedClassXmlElement = xmlDocument.createElement(XML_ATTRIBUTE_INHERITED_CLASS);
        rootElement.appendChild(inheritedClassXmlElement);
        if (!inheritedClass.getSuperclass().equals(Object.class)) {
            createInheritedClassXml(inheritedClass.getSuperclass(), inheritedClassXmlElement, object, persistenceManager);
        }
        // write superclass name
        inheritedClassXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_NAME, inheritedClass.getName());
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
            Element fieldXmlElement = xmlDocument.createElement(XML_ATTRIBUTE_FIELD);
            objectXmlElement.appendChild(fieldXmlElement);
            fieldXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_NAME, field.getName());


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
        String query = "/" + XML_ELEMENT_ROOT + "/"
                + XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]/"
                + XML_ATTRIBUTE_FIELD + "[@" + PersistenceContext.XML_ATTRIBUTE_NAME + "=\"" + name + "\"]";
        System.out.println(query);
        return queryXMLModel(query);
    }

    @Override
    public Object getObjectById(Long objectId) {
        if (idToObject.containsKey(objectId)) {
            return idToObject.get(objectId);
        }
        Element objectElement = getObjectNodeById(objectId);
        return loadObject(objectElement);
    }

    private Object loadObject(Element objectElement) {
        Object newObj = objectInstantiator.newInstance();
        //persistenceContext.registerReference(ClassHelper.createReferenceString(newObj.getClass(), objectId), newObj);

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
            Element nextSuperClassXmlElement = XmlHelper.getChildByNameAndAttribute(classXmlElement, XML_ATTRIBUTE_INHERITED_CLASS, PersistenceContext.XML_ATTRIBUTE_NAME, nextSuperclass.getName());
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
                Element fieldXmlElement = XmlHelper.getChildByNameAndAttribute(parent, XML_ATTRIBUTE_FIELD, PersistenceContext.XML_ATTRIBUTE_NAME, field.getName());
                boolean accessibility = field.canAccess(newObject);
                field.setAccessible(true);

                // TODO support collections and non primitive types
                if (fieldXmlElement.hasAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL)) {
                    field.set(newObject, null);
                } else if (field.getType().isEnum()) {
                    String fieldValue = fieldXmlElement.getTextContent();
                    Object[] enumConstants = field.getType().getEnumConstants();
                    boolean found = false;
                    for (Object enumValue : enumConstants) {
                        if (fieldValue.equals(enumValue.toString())) {
                            field.set(newObject, enumValue);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new PersistenceException("Unknown value " + fieldValue + "of enum " + field.getType().getName());
                    }
                } else if (ClassHelper.isSimpleValueType(field.getType())) {
                    String fieldValue = fieldXmlElement.getTextContent();
                    field.set(newObject, ConvertStringToType.convertStringToType(field.getType(), fieldValue));
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
        } catch (Exception e) {
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
