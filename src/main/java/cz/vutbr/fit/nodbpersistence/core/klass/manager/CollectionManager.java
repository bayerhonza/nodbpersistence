package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

public class CollectionManager extends AbstractClassManager {

    private static final String XML_ELEMENT_ROOT_COLLECTIONS = "collections";
    private static final String XML_ELEMENT_COLLECTION = "collection";
    private static final String XML_ELEMENT_COLLECTION_ITEM = "item";


    public CollectionManager(PersistenceContext persistenceContext,Class<?> persistedClass, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler, XML_ELEMENT_ROOT_COLLECTIONS);
    }


    @Override
    public String getRootXmlElementName() {
        return XML_ELEMENT_ROOT_COLLECTIONS;
    }

    @Override
    public String getItemXmlElementName() {
        return XML_ELEMENT_COLLECTION;
    }

    @Override
    public void performPersist(PersistEntityEvent persistEntityEvent) {

    }

    @Override
    public Object performLoad(Long objectId) {
        return null;
    }

    @Override
    public void persistObject(Object object, PersistenceManager persistenceManager) {
        Collection collectionObject = (Collection) object;
        Element collectionXmlElement = xmlDocument.createElement(XML_ELEMENT_COLLECTION);
        rootElement.appendChild(collectionXmlElement);
        Long collectionId = idGenerator.getNextId();
        collectionXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, collectionId.toString());
        collectionXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, collectionObject.getClass().getName());

        createXMLCollection(collectionObject, collectionXmlElement, persistenceManager);
        registerObject(collectionObject, collectionId);

        try {
            flushXMLDocument();
        } catch (TransformerException | FileNotFoundException e) {
            throw new PersistenceException(e);
        }

    }

    @Override
    public Object getObjectById(Long objectId) {
        Element collectionElement = getObjectNodeById(objectId);
        try {
            return loadCollection(collectionElement);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Long getObjectId(Object object) {
        return null;
    }


    private void createXMLCollection(Collection collection, Element parentField, PersistenceManager persistenceManager) {
        for (Object o : collection) {
            Element xmlItemElement = xmlDocument.createElement(XML_ELEMENT_COLLECTION_ITEM);
            parentField.appendChild(xmlItemElement);
            createXMLStructure(xmlItemElement, o, persistenceManager);
        }
    }

    @SuppressWarnings("unchecked")
    public Object loadCollection(Element node) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Long arrayId = Long.valueOf(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID));
        System.out.println(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS));
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

}
