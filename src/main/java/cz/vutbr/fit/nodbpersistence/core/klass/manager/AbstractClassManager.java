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
import javax.xml.xpath.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractClassManager {

    protected final PersistenceContext persistenceContext;

    final Class<?> persistedClass;


    protected final HashMap<Long, Node> idToNode = new HashMap<>();
    protected final HashMap<Object,Long> objectToId = new HashMap<>();
    protected final HashMap<Long,Object> idToObject = new HashMap<>();
    protected final HashSet<String> objectsInProgress = new HashSet<>();
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
            refreshPersistedObjects(getRootXmlElementName(),getItemXmlElementName());
        }
    }
    public abstract String getRootXmlElementName();

    public abstract String getItemXmlElementName();

    public abstract void performPersist(PersistEntityEvent persistEntityEvent);

    public abstract void persistObject(Object object,PersistenceManager persistenceManager);

    public abstract Object getObjectById(Long objectId);

    public abstract Long getObjectId(Object object);

    public Class<?> getHandledClass() {
        return this.persistedClass;
    }

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
        rootElement.setAttribute(PersistenceContext.XML_ATTRIBUTE_NAME,persistedClass.getName());
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

    public Object performLoad(Long objectId) {
        return getObjectById(objectId);
    }

    public boolean isAlreadyPersisted(Long objectId) {
        return idToNode.containsKey(objectId);
    }

    public boolean isInProgress(String reference) {
        return objectsInProgress.contains(reference);
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
        Node node = queryXMLModel("/" + getRootXmlElementName() + "/" + getItemXmlElementName() + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]");
        if (node instanceof Element) {
            return (Element) node;
        } else {
            throw new PersistenceException("Object node with ObjectId " + objectId + "not found.");
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
        if (object == null) {           // null for all null values
            xmlField.setAttribute(PersistenceContext.XML_ATTRIBUTE_ISNULL, Boolean.TRUE.toString());
        }else if (object.getClass().isEnum() || ClassHelper.isSimpleValueType(object.getClass())) { // if object type is enum, primitive or its wrapper
            xmlField.appendChild(xmlDocument.createTextNode(object.toString()));
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

    protected void refreshPersistedObjects(String rootElementName, String itemElementName) {
        initXMLDocumentBuilder();
        initXMLTransformer();
        try {
            xmlDocument = documentBuilder.parse(fileHandler.getXmlClassFile());
            Element rootElementLocal = xmlDocument.getDocumentElement();
            if (rootElementLocal.getNodeName().equals(rootElementName)) {
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
            NodeList objectNodes = rootElement.getElementsByTagName(itemElementName);
            for (int i = 0; i < objectNodes.getLength(); i++) {
                Node node = objectNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Long objectId = Long.parseLong(((Element) node).getAttribute(PersistenceContext.XML_ATTRIBUTE_OBJECT_ID));
                    idToNode.put(objectId, node);
                }
            }
            normalizeXMLModel();


        } catch (SAXException | IOException | XMLParseException e) {

        }
    }

    protected Object loadObjectFromElement(Element element) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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

    public String persistAndGetReference(Object object, PersistenceManager persistenceManager) {
        if (!persistedClass.isAssignableFrom(object.getClass())) {
            throw new PersistenceException("Bad class manager.");
        }
        if (!objectToId.containsKey(object)) {
            persistObject(object, persistenceManager);
        }
        return getFullReference(object);
    }

    public void registerTempReference(String reference) {

    }
}
