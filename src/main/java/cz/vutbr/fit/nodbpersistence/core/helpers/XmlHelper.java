package cz.vutbr.fit.nodbpersistence.core.helpers;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHelper {

    public static Element getChildByNameAndAttribute(Node parent, String elementName, String atributeName, String attributeValue) throws XmlException {
        Node childNode = parent.getFirstChild();
        while (childNode != null) {
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                childNode = childNode.getNextSibling();
            }
            Element childElement = (Element) childNode;
            if (!childElement.getNodeName().equals(elementName)) {
                childNode = childNode.getNextSibling();
                continue;
            }

            Attr attribute = childElement.getAttributeNode(atributeName);
            if (attribute != null && attribute.getTextContent().equals(attributeValue)) {
                return childElement;
            }
            childNode = childNode.getNextSibling();
        }
        throw new XmlException("Attribute " + atributeName + " with value " +attributeValue + " in element " + elementName + " was not found.");

    }

    public static Element getElementByAttribute(NodeList nodeList, String elementName, String atributeName, String attributeValue) throws PersistenceException, XmlException {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node xmlField = nodeList.item(i);
            if (xmlField.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element xmlElementField = (Element) xmlField;
            if (!xmlElementField.getNodeName().equals(elementName)) {
                continue;
            }

            Attr attribute = xmlElementField.getAttributeNode(atributeName);
            if (attribute != null && attribute.getTextContent().equals(attributeValue)) {
                return xmlElementField;
            }
        }
        throw new XmlException("Attribute " + atributeName + " with value " +attributeValue + " in element " + elementName + " was not found.");
    }

    public static Element getNextElement(Element element) {
        Node node = element.getNextSibling();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
            node = node.getNextSibling();
        }
        return null;
    }
}
