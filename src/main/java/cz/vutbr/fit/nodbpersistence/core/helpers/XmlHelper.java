package cz.vutbr.fit.nodbpersistence.core.helpers;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Utility for actions with XML model.
 */
public class XmlHelper {

    /**
     * Method for getting an element from XML node with given tag name, its attribute and its value.
     *
     * @param parent         parent XML node
     * @param elementName    element name
     * @param attributeName  attribute name
     * @param attributeValue attribute value
     * @return first found element
     * @throws XmlException if no element is found
     */
    public static Element getChildByNameAndAttribute(Node parent, String elementName, String attributeName, String attributeValue) throws XmlException {
        Node childNode = parent.getFirstChild();
        while (childNode != null) {
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                childNode = childNode.getNextSibling();
                continue;
            }
            Element childElement = (Element) childNode;
            if (!childElement.getNodeName().equals(elementName)) {
                childNode = childNode.getNextSibling();
                continue;
            }

            Attr attribute = childElement.getAttributeNode(attributeName);
            if (attribute != null && attribute.getTextContent().equals(attributeValue)) {
                return childElement;
            }
            childNode = childNode.getNextSibling();
        }
        throw new XmlException("Attribute " + attributeName + " with value " + attributeValue + " in element " + elementName + " was not found.");

    }

    /**
     * Return next sibling of defined element.
     *
     * @param element original element
     * @return original element's next sibling
     */
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
