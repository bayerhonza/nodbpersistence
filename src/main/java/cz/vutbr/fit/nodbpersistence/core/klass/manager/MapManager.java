package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.helpers.XmlHelper;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Class manager for maps.
 */
public class MapManager extends AbstractClassManager {

    public static final String XML_ELEMENT_ROOT_MAP = "maps";
    public static final String XML_ELEMENT_MAP = "map";

    public static final String XML_ELEMENT_MAP_ENTRY = "entry";
    public static final String XML_ELEMENT_MAP_KEY = "key";
    public static final String XML_ELEMENT_MAP_VALUE = "value";

    public MapManager(PersistenceContext persistenceContext, Class<?> persistedClass, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        super(persistenceContext, xmlFileExists, persistedClass, classFileHandler);
    }

    @Override
    public String getRootXmlElementName() {
        return XML_ELEMENT_ROOT_MAP;
    }

    @Override
    public String getItemXmlElementName() {
        return XML_ELEMENT_MAP;
    }

    @Override
    public Long performPersist(PersistEntityEvent persistEntityEvent) {
        return null;
    }

    @Override
    public void persistObject(Object object, PersistenceManager persistenceManager) {
        Map map = (Map) object;
        Element mapXmlElement = xmlDocument.createElement(XML_ELEMENT_MAP);
        rootElement.appendChild(mapXmlElement);
        Long mapId = idGenerator.getNextId();
        mapXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID, mapId.toString());
        mapXmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, map.getClass().getName());
        createXMLMap(map, mapXmlElement, persistenceManager);
        registerObject(map,mapId);

    }

    @Override
    public Object getObjectById(Long objectId) {
        if (idToObject.containsKey(objectId)) {
            return idToObject.get(objectId);
        }
        Element mapElement = getObjectNodeById(objectId);
        try {
            return loadMap(mapElement);
        } catch (ReflectiveOperationException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Loads map from a given XML element
     *
     * @param node given XML node
     * @return created map
     * @throws ReflectiveOperationException reflection error
     */
    @SuppressWarnings("unchecked")
    public Object loadMap(Element node) throws ReflectiveOperationException {
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

    private void createXMLMap(Map map, Element parentField, PersistenceManager persistenceManager) {
        for (Object key : map.keySet()) {
            Element entryXmlElement = xmlDocument.createElement(XML_ELEMENT_MAP_ENTRY);
            parentField.appendChild(entryXmlElement);
            Object value = map.get(key);

            createXMLField(entryXmlElement, key, XML_ELEMENT_MAP_KEY, persistenceManager);
            createXMLField(entryXmlElement, value, XML_ELEMENT_MAP_VALUE, persistenceManager);
        }
    }

    private void createXMLField(Element rootXmlElement, Object value, String xmlElementName, PersistenceManager persistenceManager) {
        Element xmlElement = xmlDocument.createElement(xmlElementName);
        rootXmlElement.appendChild(xmlElement);
        if (value == null) {
            xmlElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL, Boolean.TRUE.toString());
            return;
        }

        createXMLStructure(xmlElement, value, persistenceManager);
    }


}
