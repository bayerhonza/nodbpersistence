package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;

/**
 * Class manager for arrays.
 */
public class ArrayManager extends AbstractClassManager {

    private final static String XML_ELEMENT_ARRAY_ROOT = "arrays";
    private final static String XML_ELEMENT_ARRAY = "array";
    private final static String XML_ELEMENT_ARRAY_ITEM = "item";

    private final static String XML_ATTRIBUTE_SIZE = "size";

    public ArrayManager(PersistenceContext persistenceContext, boolean xmlFileExists, Class<?> persistedClass, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler);
    }

    @Override
    public String getRootXmlElementName() {
        return XML_ELEMENT_ARRAY_ROOT;
    }

    @Override
    public String getItemXmlElementName() {
        return XML_ELEMENT_ARRAY;

    }

    @Override
    public Long performPersist(PersistEntityEvent persistEntityEvent) {
        return null;
    }


    @Override
    public Object performLoad(Long objectId) {
        return null;
    }

    @Override
    public void persistObject(Object array, PersistenceManager persistenceManager) {
        Object[] arrayObject = (Object[]) array;
        Long arrayId = idGenerator.getNextId();
        registerObject(array,arrayId);
        Element arrayXmlElement = xmlDocument.createElement(XML_ELEMENT_ARRAY);
        rootElement.appendChild(arrayXmlElement);
        arrayXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, arrayId.toString());
        arrayXmlElement.setAttribute(XML_ATTRIBUTE_SIZE,Integer.valueOf(arrayObject.length).toString());
        arrayXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS,array.getClass().getName());
        createXMLArray(arrayObject, arrayXmlElement, persistenceManager);

    }

    /**
     * Loads an array from a given element
     *
     * @param node element of array
     * @return object of array
     * @throws ReflectiveOperationException reflection error
     */
    public Object loadArray(Element node) throws ReflectiveOperationException {
        Class<?> instClass = Class.forName(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS)).getComponentType();
        Long arrayId = Long.valueOf(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID));
        Integer arraySize = Integer.valueOf(node.getAttribute(XML_ATTRIBUTE_SIZE));
        Object newArray = Array.newInstance(instClass, arraySize);
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
        registerObject(newArray, arrayId);
        return newArray;

    }

    @Override
    public Object getObjectById(Long objectId) {
        if (idToObject.containsKey(objectId)) {
            return idToObject.get(objectId);
        }
        Element arrayElement = getObjectNodeById(objectId);

        try {
            return loadArray(arrayElement);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    private void createXMLArray(Object[] array, Element parentField, PersistenceManager persistenceManager) {
        for (Object o : array) {
            Element xmlItemElement = xmlDocument.createElement(XML_ELEMENT_ARRAY_ITEM);
            if (o == null) {
                xmlItemElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL,Boolean.TRUE.toString());
                continue;
            }
            parentField.appendChild(xmlItemElement);
            createXMLStructure(xmlItemElement,o,persistenceManager);
        }
    }


}
