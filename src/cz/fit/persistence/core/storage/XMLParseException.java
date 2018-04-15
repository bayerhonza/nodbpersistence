package cz.fit.persistence.core.storage;

/**
 * Internal XML parsing error.
 */
public class XMLParseException extends Exception {

    public XMLParseException(Throwable th) {
        super(th);
    }

    public XMLParseException() {
        super();
    }
}
