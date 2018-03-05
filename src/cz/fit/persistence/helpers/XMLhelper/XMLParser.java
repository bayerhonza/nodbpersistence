package cz.fit.persistence.helpers.XMLhelper;

import cz.fit.persistence.exceptions.PersistenceException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.Map;

public class XMLParser {

    public static Map<String, String> getParamsFromXML(Path pathToXML, String paramsElementName) throws PersistenceException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newDefaultInstance();

            dbFactory.setValidating(true);
            dbFactory.setIgnoringComments(true);


            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document document = builder.parse(pathToXML.toString());

            document.getDocumentElement().normalize();


        } catch (Exception ex) {
            throw new PersistenceException(ex);
        }

        return null;
    }
}
