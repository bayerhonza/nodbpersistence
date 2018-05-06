package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.storage.ClassFileHandler;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.objenesis.ObjenesisStd;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPathFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CollectionManager extends AbstractClassManager {


    public CollectionManager(PersistenceContext persistenceContext, boolean xmlFileExists, ClassFileHandler classFileHandler) {
        super(persistenceContext,xmlFileExists,Collection.class,classFileHandler);
        if (!xmlFileExists) {
            initXMLDocument(persistedClass);
            initXMLTransformer();
        } else {
            refreshPersistedObjects();
        }
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

    @Override
    public void initXMLDocument(Class<?> persistedClass) {

    }
}
