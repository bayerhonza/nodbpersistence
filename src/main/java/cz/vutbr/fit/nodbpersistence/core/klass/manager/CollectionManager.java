package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class CollectionManager extends AbstractClassManager {

    private static final String XML_ELEMENT_ROOT_COLLECTIONS = "collections";
    private static final String XML_ELEMENT_COLLECTION = "collection";
    private static final String XML_ELEMENT_COLLECTION_ITEM = "item";


    public CollectionManager(PersistenceContext persistenceContext,Class<?> persistedClass, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler);
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
    public Long performPersist(PersistEntityEvent persistEntityEvent) {
        return null;
    }

    @Override
    public Object performLoad(Long objectId) {
        return null;
    }

    @Override
    public void persistObject(Object collection, PersistenceManager persistenceManager) {
        Collection collectionObject = (Collection) collection;
        Element collectionXmlElement = xmlDocument.createElement(XML_ELEMENT_COLLECTION);
        rootElement.appendChild(collectionXmlElement);
        Long collectionId = idGenerator.getNextId();

        registerPersistedObject(collectionObject, collectionId);
        collectionXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, collectionId.toString());
        collectionXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, collectionObject.getClass().getName());

        createXMLCollection(collectionObject, collectionXmlElement, persistenceManager);
        assignIdToElement(collectionId, collectionXmlElement);
    }

    @Override
    public Object getObjectById(Long objectId) {
        if (loadCache.containsKey(objectId)) {
            return loadCache.get(objectId);
        }
        Element collectionElement = getObjectNodeById(objectId);
        try {
            return loadCollection(collectionElement);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Loads collection from a given node.
     *
     * @param node node with collection
     * @return collection
     * @throws ReflectiveOperationException reflection error
     */
    @SuppressWarnings("unchecked")
    public Object loadCollection(Element node) throws ReflectiveOperationException {
        Class<?> collectionClass = Class.forName(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS));
        Long collectionId = Long.valueOf(node.getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID));
        Constructor collectionConstructor = collectionClass.getConstructor();
        Collection newCollection = (Collection) collectionConstructor.newInstance();
        registerLoadedObject(newCollection, collectionId);
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

    private void createXMLCollection(Collection collection, Element parentField, PersistenceManager persistenceManager) {
        for (Object o : collection) {
            Element xmlItemElement = xmlDocument.createElement(XML_ELEMENT_COLLECTION_ITEM);
            if (o == null) {
                xmlItemElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL, Boolean.TRUE.toString());
                continue;
            }
            parentField.appendChild(xmlItemElement);
            createXMLStructure(xmlItemElement, o, persistenceManager);
        }
    }



}
