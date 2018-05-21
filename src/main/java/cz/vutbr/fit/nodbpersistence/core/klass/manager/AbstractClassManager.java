package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.core.helpers.ConvertStringToType;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.core.storage.XMLParseException;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
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
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Abstract class for class managers.
 */
public abstract class AbstractClassManager {

    protected final PersistenceContext persistenceContext;

    protected final Class<?> persistedClass;

    // map mapping objectId to its XML element
    protected final HashMap<Long, Element> persistedIds = new HashMap<>();
    // map mapping object to its objectId.

    protected final IdentityHashMap<Object, Long> persistCache = new IdentityHashMap<>();
    // mapping objectId to its object
    protected final HashMap<Long, Object> loadCache = new HashMap<>();

    // objectId generator
    IdGenerator idGenerator;

    final ClassFileHandler fileHandler;

    // XML model part
    private DocumentBuilder documentBuilder;
    private Transformer transformer;
    protected XPathFactory xPathFactory = XPathFactory.newInstance();
    protected Document xmlDocument;
    protected Element rootElement;


    public AbstractClassManager(PersistenceContext persistenceContext, boolean xmlFileExists, Class<?> persistedClass, ClassFileHandler classFileHandler) {
        this.persistenceContext = persistenceContext;
        this.persistedClass = persistedClass;
        this.fileHandler = classFileHandler;
        // if XML file exists, the objects are automatically loaded to memory
        if (!xmlFileExists) {
            initXMLTransformer();
            initXMLDocument();
        } else {
            refreshPersistedObjects();
        }
    }

    /**
     * Tries to persist object. If the object is already persisted, it reaturns reference as a string.
     * Otherwise it starts persisting the object.
     *
     * @param object             object to be persisted
     * @param persistenceManager source persistence manager
     * @return reference to the object as a string
     */
    public String persistAndGetReference(Object object, PersistenceManager persistenceManager) {
        persistenceManager.addModifiedClassManager(this);
        if (!persistedClass.isAssignableFrom(object.getClass())) {
            throw new PersistenceException("Bad class manager.");
        }
        if (!persistCache.containsKey(object)) {
            persistObject(object, persistenceManager);
        }
        return getFullReference(object);
    }

    /**
     * Get root XML element for the class.
     *
     * @return root XML element.
     */
    public abstract String getRootXmlElementName();

    /**
     * Get item XML element for the class.
     *
     * @return item XML element
     */
    public abstract String getItemXmlElementName();

    /**
     * Processes the persist event.
     *
     * @param persistEntityEvent persist event
     */
    public abstract Long performPersist(PersistEntityEvent persistEntityEvent);

    /**
     * Persists given object in one persistence manager (source)
     *
     * @param object             object to be persisted
     * @param persistenceManager source persistence manager
     */
    public abstract void persistObject(Object object, PersistenceManager persistenceManager);

    /**
     * Finds object based on its objectId.
     *
     * @param objectId ojbectId
     * @return object with defined objectId.
     */
    public abstract Object getObjectById(Long objectId);

    /**
     * Get class handled by this class manager.
     *
     * @return handled class
     */
    public Class<?> getHandledClass() {
        return this.persistedClass;
    }

    /**
     * Flushes the modifications and writes XML file to disk.
     */
    public void flushXMLDocument() {
        try (OutputStream fos = fileHandler.getXMLOutputStream()) {
            DOMSource source = new DOMSource(xmlDocument);
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        } catch (IOException | TransformerException e) {
            throw new PersistenceException(e);
        }
    }

    public void cleanLoadCache() {
        this.loadCache.clear();
    }

    /**
     * Launches loading.
     *
     * @param objectId objectId of loaded object
     * @return loaded object
     */
    public Object performLoad(Long objectId) {
        if (!persistedIds.containsKey(objectId)) {
            throw new PersistenceException("No object of class " + this.persistedClass.getCanonicalName() + " with objectId " + objectId + " found.");
        }
        return getObjectById(objectId);
    }

    /**
     * Checks if the objectId is already persisted.
     *
     * @param objectId objectId
     * @return true if present, false if not
     */
    public boolean isAlreadyPersisted(Long objectId) {
        return persistedIds.containsKey(objectId);
    }

    /**
     * Register object as persisted.
     *
     * @param object   object
     * @param objectId objectId of the object
     */
    public void registerPersistedObject(Object object, Long objectId) {
        this.persistCache.put(object, objectId);
    }

    /**
     * Register object as loaded.
     *
     * @param object   object
     * @param objectId objectId of the object
     */
    public void registerLoadedObject(Object object, Long objectId) {
        this.loadCache.put(objectId, object);
    }

    /**
     * Get object element based on objectId.
     *
     * @param objectId objectId
     * @return XML element with object
     */
    public Element getObjectNodeById(Long objectId) {
        return persistedIds.get(objectId);
    }

    /**
     * Return reference to the object in format className#objectId.
     *
     * @param object object
     * @return reference as a string, {@code null} if object is not persisted
     */
    public String getFullReference(Object object) {
        if (persistCache.containsKey(object)) {
            return persistedClass.getName() + "#" + persistCache.get(object).toString();
        } else {
            return null;
        }
    }

    /**
     * Assigns objectId to XML element in inner model.
     *
     * @param objectId      objectId
     * @param objectElement element in model
     */
    public void assignIdToElement(Long objectId, Element objectElement) {
        persistedIds.put(objectId, objectElement);
    }

    /**
     * Dereferences given string and finds desired object.
     *
     * @param reference reference as a string
     * @return references object
     * @throws ClassNotFoundException internal error
     */
    public Object getObjectByReference(String reference) throws ClassNotFoundException {
        String[] parsedReference = reference.split("#");
        String className = parsedReference[0];
        Long cascadeObjectId = Long.parseLong(parsedReference[1]);
        Class<?> referencedClass = Class.forName(className);
        AbstractClassManager cascadeObjectManager = persistenceContext.findClassManager(referencedClass);
        return cascadeObjectManager.getObjectById(cascadeObjectId);
    }

    /**
     * Creates an XML in-memory model of object.
     *
     * @param xmlField           root field for XML
     * @param object             object to be stored
     * @param persistenceManager persistence manager of event to store the object if part of collection
     */
    protected void createXMLStructure(Element xmlField, Object object, PersistenceManager persistenceManager) {
        if (object == null) { // null for all null value
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL, Boolean.TRUE.toString());
        } else if (object.getClass().isEnum() || ClassHelper.isSimpleValueType(object.getClass())) { // if object type is enum, primitive or its wrapper
            if (ClassHelper.isSimpleValueType(object.getClass())) {
                xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_COLL_INST_CLASS, object.getClass().getName());
            }
            xmlField.setTextContent(object.toString());
        } else {
            AbstractClassManager classManager;
            if (object.getClass().isArray()) { // is array
                classManager = persistenceContext.getArrayManager();
            } else if (object instanceof Collection) { // type is collection
                classManager = persistenceContext.getCollectionManager();
            } else if (object instanceof Map) {
                classManager = persistenceContext.getMapManager();
            } else {
                classManager = persistenceContext.findClassManager(object.getClass());
            }
            String fullReference = classManager.persistAndGetReference(object, persistenceManager);
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, fullReference);
        }

    }

    /**
     * Reload objects into memory after parsing already existing XML file.
     */
    protected void refreshPersistedObjects() {
        initXMLDocumentBuilder();
        initXMLTransformer();
        try {
            xmlDocument = documentBuilder.parse(fileHandler.getXmlClassFile());
            Element systemRoot = xmlDocument.getDocumentElement();
            NodeList nodeList = systemRoot.getElementsByTagName(getRootXmlElementName());
            if (nodeList.getLength() != 1) {
                throw new XMLParseException("Multiple " + getRootXmlElementName() + " root element defined.");
            }
            Element rootElementLocal;
            if (nodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                rootElementLocal = (Element) nodeList.item(0);
            } else {
                throw new XMLParseException("XML bad syntax for nodbpersistence.");
            }
            if (rootElementLocal.getNodeName().equals(getRootXmlElementName())) {
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
            NodeList objectNodes = rootElement.getElementsByTagName(getItemXmlElementName());
            for (int i = 0; i < objectNodes.getLength(); i++) {
                Node node = objectNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String idString = element.getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID);
                    if (idString.equals("")) {
                        throw new XMLParseException("No object Id present");
                    }
                    persistedIds.put(Long.parseLong(idString), element);

                }
            }
            normalizeXMLModel();


        } catch (SAXException | IOException | XMLParseException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Can load an object from an XML element.
     *
     * @param element XML element
     * @return object included in the XML element
     * @throws ReflectiveOperationException if reflection fails
     */
    protected Object loadObjectFromElement(Element element) throws ReflectiveOperationException {
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
            return persistenceContext.getCollectionManager().loadCollection(element);
        } else if (instClass.isArray()) {
            return persistenceContext.getArrayManager().loadArray(element);
        } else if (Map.class.isAssignableFrom(instClass)) {
            return persistenceContext.getMapManager().loadMap(element);
        } else {
            return null;
        }
    }


    private void initXMLDocumentBuilder() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new PersistenceException(e);
        }
        xmlDocument = documentBuilder.newDocument();
    }

    private void initXMLTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new PersistenceException(e);
        }

    }

    private void initXMLDocument() {
        initXMLDocumentBuilder();
        Element systemRoot = xmlDocument.createElement(PersistenceContext.XML_NODBPERSISTENCE);
        xmlDocument.appendChild(systemRoot);
        rootElement = xmlDocument.createElement(getRootXmlElementName());
        systemRoot.appendChild(rootElement);
        rootElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_NAME, persistedClass.getName());
        Attr idGenAttr = xmlDocument.createAttribute(PersistenceContext.XML_ELEMENT_ID_GENERATOR);
        idGenerator = new IdGenerator(idGenAttr);
        rootElement.setAttributeNode(idGenAttr);

    }

    private void normalizeXMLModel() {
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                rootElement.removeChild(node);
            }
        }
    }


}
