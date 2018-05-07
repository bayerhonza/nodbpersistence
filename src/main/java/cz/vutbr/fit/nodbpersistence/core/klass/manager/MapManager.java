package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import org.w3c.dom.Element;

import java.util.Map;

public class MapManager extends AbstractClassManager {

    public static final String XML_ELEMENT_ROOT_MAP = "maps";
    public static final String XML_ELEMENT_MAP = "map";

    public MapManager(PersistenceContext persistenceContext, boolean xmlFileExists, Class<?> persistedClass, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler,XML_ELEMENT_ROOT_MAP);
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

    private void persistObject(Map map, PersistenceManager persistenceManager) {
        Element mapXmlElement = xmlDocument.createElement(XML_ELEMENT_MAP);
        rootElement.appendChild(mapXmlElement);
        Long mapId = idGenerator.getNextId();
        mapXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, mapId.toString());
        objectsInProgress.add(mapId);
        createXMLMap(map, mapXmlElement, persistenceManager);
        registerObject(map,mapId);
        objectsInProgress.remove(mapId);
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

    private void createXMLMap(Map map,Element parentField, PersistenceManager persistenceManager) {
        for (Object key : map.keySet()) {
            Element entryXmlElement = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_MAP_ENTRY);
            parentField.appendChild(entryXmlElement);
            Object value = map.get(key);

            createXMLField(entryXmlElement,key,PersistenceContext.XML_ELEMENT_MAP_KEY,persistenceManager);
            createXMLField(entryXmlElement,value,PersistenceContext.XML_ELEMENT_MAP_VALUE,persistenceManager);

                /*Element valueXmlElement = xmlDocument.createElement(PersistenceContext.XML_ELEMENT_MAP_VALUE);
                valueXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS,value.getClass().getName());
                entryXmlElement.appendChild(valueXmlElement);
                createXMLStructure(valueXmlElement,value,persistenceManager);*/
        }
    }

    private void createXMLField(Element rootXmlElement,Object value, String xmlElementName,PersistenceManager persistenceManager) {
        Element xmlElement = xmlDocument.createElement(xmlElementName);
        rootXmlElement.appendChild(xmlElement);
        if (value == null) {
            xmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL,Boolean.TRUE.toString());
            return;
        }
        xmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS,value.getClass().getName());


        createXMLStructure(xmlElement,value,persistenceManager);
    }

    public String persistAndGetReference(Map map, PersistenceManager persistenceManager) {
        if (!objectToId.containsKey(map)) {
            persistObject(map, persistenceManager);
        }
        return getFullReference(map);
    }

}
