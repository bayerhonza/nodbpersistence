package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.util.Collection;

public class CollectionManager extends AbstractClassManager {

    private static final String XML_ELEMENT_ROOT_COLLECTIONS = "collections";
    private static final String XML_ELEMENT_COLLECTION = "collection";



    public CollectionManager(PersistenceContext persistenceContext, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        super(persistenceContext,xmlFileExists,Collection.class,classFileHandler,XML_ELEMENT_ROOT_COLLECTIONS);
    }

    @Override
    void refreshPersistedObjects() {

    }

    @Override
    public Object performLoad(Long objectId) {
        return null;
    }

    @Override
    public void performPersist(PersistEntityEvent persistEntityEvent) {

    }

    private void persistObject(Collection collection,PersistenceManager persistenceManager) {
        Element collectionXmlElement = xmlDocument.createElement(XML_ELEMENT_COLLECTION);
        rootElement.appendChild(collectionXmlElement);
        Long collectionId = idGenerator.getNextId();
        collectionXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, collectionId.toString());
        collectionXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, collection.getClass().getName());
        objectsInProgress.add(collectionId);

        createXMLCollection(collection,collectionXmlElement,persistenceManager);
        registerObject(collection,collectionId);
        objectsInProgress.remove(collectionId);

        try {
            flushXMLDocument();
        } catch (TransformerException | FileNotFoundException e) {
            throw new PersistenceException(e);
        }

    }

    @Override
    public Element getObjectNodeById(Long objectId) {
        return null;
    }

    @Override
    public Object getObjectById(Long objectId) {
        return null;
    }

    @Override
    public Long getObjectId(Object object) {
        return null;
    }

    @Override
    public Object getObjectByReference(String reference) throws ClassNotFoundException {
        return null;
    }

    @Override
    public Long isPersistentOrInProgress(Object object) {
        return null;
    }



    private void createXMLCollection(Collection collection, Element parentField, PersistenceManager persistenceManager) {
        for (Object o : collection) {
            Element xmlItemElement = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_COLLECTION_ITEM);
            parentField.appendChild(xmlItemElement);
            createXMLStructure(xmlItemElement,o,persistenceManager);
        }
    }

    public String persistAndGetReference(Collection collection,PersistenceManager persistenceManager) {
        persistObject(collection,persistenceManager);
        return getFullReference(collection);
    }


}
