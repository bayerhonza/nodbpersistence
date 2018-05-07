package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Map;

public class ArrayManager extends AbstractClassManager {

    private final static String XML_ELEMENT_ARRAY_ROOT = "arrays";
    private final static String XML_ELEMENT_ARRAY_ITEM = "array";

    private final static String XML_ATTRIBUTE_SIZE = "size";
    private final static String XML_ATTRIBUTE_INST_CLASS = "inst";

    public ArrayManager(PersistenceContext persistenceContext, boolean xmlFileExists, Class<?> persistedClass, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler,PersistenceContext.XML_ELEMENT_ROOT);
    }

    @Override
    void refreshPersistedObjects() {

    }

    @Override
    public Object performLoad(Long objectId) {
        return null;
    }

    @Override
    public void performPersist(PersistEntityEvent persistEvent) {
        Object persistedArray = persistEvent.getObject();
    }

    private void persistObject(Object[] array, PersistenceManager persistenceManager) {
        Element arrayXmlElement = xmlDocument.createElement(XML_ELEMENT_ARRAY_ITEM);
        rootElement.appendChild(arrayXmlElement);
        Long arrayId = idGenerator.getNextId();
        arrayXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, arrayId.toString());
        arrayXmlElement.setAttribute(XML_ATTRIBUTE_SIZE,Integer.valueOf(array.length).toString());
        objectsInProgress.add(arrayId);
        createXMLArray(array, arrayXmlElement, persistenceManager);
        registerObject(array,arrayId);
        objectsInProgress.remove(arrayId);

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

    private void createXMLArray(Object[] array, Element parentField, PersistenceManager persistenceManager) {
        for (Object o : array) {
            Element xmlItemElement = xmlDocument.createElement(PersistenceContext.XML_ATTRIBUTE_COLLECTION_ITEM);
            parentField.appendChild(xmlItemElement);
            createXMLStructure(xmlItemElement,o,persistenceManager);
        }
    }

    public String persistAndGetReference(Object[] object, PersistenceManager persistenceManager) {
        persistObject(object, persistenceManager);
        return getFullReference(object);
    }
}
