package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.helpers.ClassHelper;
import cz.vutbr.fit.nodbpersistence.core.helpers.HashHelper;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.objenesis.ObjenesisStd;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.FileNotFoundException;
import java.util.*;

public abstract class AbstractClassManager {

    protected final PersistenceContext persistenceContext;

    final Class<?> persistedClass;


    final HashMap<Long, Node> persistedObjects = new HashMap<>();
    final HashMap<Object,Long> objectToId = new HashMap<>();
    final HashMap<Long,Object> idToObject = new HashMap<>();
    final Set<Long> objectsInProgress = new HashSet<>();
    IdGenerator idGenerator;

    final ClassFileHandler fileHandler;

    // XML model
    protected DocumentBuilder documentBuilder;
    protected Document xmlDocument;
    protected Element rootElement;
    protected Transformer transformer;
    protected XPathFactory xPathFactory = XPathFactory.newInstance();

    public AbstractClassManager(PersistenceContext persistenceContext, boolean xmlFileExists, Class<?> persistedClass, ClassFileHandler classFileHandler, String xmlRoot) {
        this.persistenceContext = persistenceContext;
        this.persistedClass = persistedClass;
        this.fileHandler = classFileHandler;
        if (!xmlFileExists) {
            initXMLTransformer();
            initXMLDocument(persistedClass,xmlRoot);

        } else {
            refreshPersistedObjects();
        }
    }

    abstract void refreshPersistedObjects();

    public abstract Object performLoad(Long objectId);

    public abstract void performPersist(PersistEntityEvent persistEntityEvent);

    public abstract Object getObjectById(Long objectId);

    public abstract Long getObjectId(Object object);

    public Class<?> getHandledClass() {
        return this.persistedClass;
    }

    public abstract Long isPersistentOrInProgress(Object object);

    void initXMLDocumentBuilder() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new PersistenceException(e);
        }
        xmlDocument = documentBuilder.newDocument();
    }

    void initXMLTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            throw new PersistenceException(e);
        }

    }

    protected void initXMLDocument(Class<?> persistedClass, String rootElementName) {
        initXMLDocumentBuilder();
        rootElement = xmlDocument.createElement(rootElementName);
        Attr idGenAttr = xmlDocument.createAttribute(PersistenceContext.XML_ELEMENT_ID_GENERATOR);
        idGenerator = new IdGenerator(idGenAttr);
        rootElement.setAttributeNode(idGenAttr);
        xmlDocument.appendChild(rootElement);
    }
    /**
     * Delete whitespace nodes from XML.
     */
    void normalizeXMLModel() {
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                rootElement.removeChild(node);
            }
        }
    }

    void flushXMLDocument() throws TransformerException, FileNotFoundException {
        DOMSource source = new DOMSource(xmlDocument);
        StreamResult result = new StreamResult(fileHandler.getXMLOutputStream());
        transformer.transform(source, result);
    }

    public boolean isAlreadyPersisted(Long objectId) {
        return persistedObjects.containsKey(objectId);
    }


    Node queryXMLModel(String xmlPathQuery) {
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

    public void registerObject(Object object, Long objectId) {
        this.idToObject.put(objectId,object);
        this.objectToId.put(object,objectId);
    }

    public Element getObjectNodeById(Long objectId) {
        Node node = queryXMLModel("/" + PersistenceContext.XML_ELEMENT_ROOT + "/" + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]");
        if (node instanceof Element) {
            return (Element) node;
        } else {
            throw new PersistenceException("Object node with ObjectId " + objectId + "not found.");
        }
    }

    protected String startCascade(Object object, PersistenceManager persistenceManager) {
        String reference = persistenceContext.getReferenceIfPersisted(object);
        if (reference == null) {
            persistenceManager.persist(object);
            Long objectId = persistenceContext.findClassManager(object.getClass()).getObjectId(object);
            return object.getClass().getName() + "#" + objectId;
        } else {
            return reference;
        }
    }

    public String getFullReference(Object object) {
        if (objectToId.containsKey(object)) {
            return persistedClass.getName() + "#" + objectToId.get(object).toString();
        } else {
            return null;
        }
    }

    public Object getObjectByReference(String reference) throws ClassNotFoundException {
        if (persistenceContext.isReferenceRegistered(reference)) {
            return persistenceContext.getObjectByReference(reference);
        }
        String[] parsedReference = reference.split("#");
        String className = parsedReference[0];
        Long cascadeObjectId = Long.parseLong(parsedReference[1]);
        Class<?> referencedClass = Class.forName(className);
        if (referencedClass.equals(persistedClass)) {
            return getObjectById(cascadeObjectId);
        } else {
            AbstractClassManager cascadeObjectManager = persistenceContext.findClassManager(referencedClass);
            return cascadeObjectManager.getObjectById(cascadeObjectId);
        }
    }

    /**
     * Creates an XML in-memory model of object.
     *
     * @param xmlField           root field for XML
     * @param object             object to be stored
     * @param persistenceManager persistence manager of event to store the object if part of collection
     */
    protected void createXMLStructure(Element xmlField, Object object, PersistenceManager persistenceManager) {
        if (object == null) {           // null for all null values
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL, Boolean.TRUE.toString());
        }else if (object.getClass().isEnum() || ClassHelper.isSimpleValueType(object.getClass())) { // if object type is enum, primitive or its wrapper
            xmlField.appendChild(xmlDocument.createTextNode(object.toString()));
        } else if (object.getClass().isArray()) { // is array
            Object[] castedArray = (Object[]) object;
            String fullReference = persistenceContext.getArrayManager().persistAndGetReference(castedArray,persistenceManager);
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, fullReference);
        } else if (object instanceof Collection) { // type is collection
            Collection castedCollection = (Collection) object;
            String fullReference = persistenceContext.getCollectionManager().persistAndGetReference(castedCollection,persistenceManager);
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, fullReference);
        } else if (object instanceof Map) {
            String fullReference = persistenceContext.getMapManager().persistAndGetReference((Map) object,persistenceManager);
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, fullReference);
        } else {
            String reference = startCascade(object, persistenceManager);
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_FIELD_REFERENCE, reference);
        }

    }
}
