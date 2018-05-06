package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractClassManager {

    protected final PersistenceContext persistenceContext;

    final Class<?> persistedClass;


    final HashMap<Long, Node> persistedObjects = new HashMap<>();
    final Set<Long> objectsInProgress = new HashSet<>();
    IdGenerator idGenerator;

    final ClassFileHandler fileHandler;

    // XML model
    protected DocumentBuilder documentBuilder;
    protected Document xmlDocument;
    protected Element rootElement;
    protected Transformer transformer;
    protected XPathFactory xPathFactory = XPathFactory.newInstance();

    public AbstractClassManager(PersistenceContext persistenceContext, boolean xmlFileExists, Class<?> persistedClass, ClassFileHandler classFileHandler) {
        this.persistenceContext = persistenceContext;
        this.persistedClass = persistedClass;
        this.fileHandler = classFileHandler;
        if (!xmlFileExists) {
            initXMLTransformer();
        } else {
            refreshPersistedObjects();
        }
    }

    abstract void refreshPersistedObjects();

    public abstract Object performLoad(Long objectId);

    public abstract void performPersist(PersistEntityEvent persistEntityEvent);

    public abstract Object getObjectById(Long objectId);

    public abstract Long getObjectId(Object object);

    public abstract  Object getObjectByReference(String reference) throws ClassNotFoundException;

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

    public abstract void initXMLDocument(Class<?> persistedClass);

    void initXMLTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            throw new PersistenceException(e);
        }

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

    public Element getObjectNodeById(Long objectId) {
        Node node = queryXMLModel("/" + PersistenceContext.XML_ELEMENT_ROOT + "/" + PersistenceContext.XML_ELEMENT_OBJECT + "[@" + PersistenceContext.XML_ATTRIBUTE_OBJECT_ID + "=" + objectId + "]");
        if (node instanceof Element) {
            return (Element) node;
        } else {
            throw new PersistenceException("Object node with ObjectId " + objectId + "not found.");
        }

    }
}
