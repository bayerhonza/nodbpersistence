package cz.vutbr.fit.nodbpersistence.core.storage;

/**
 * Internal XML parsing error.
 */
public class XMLParseException extends Exception {

    public XMLParseException(Throwable th) {
        super(th);
    }

    public XMLParseException(String msg) {
        super(msg);
    }

    public XMLParseException() {
        super();
    }
}
