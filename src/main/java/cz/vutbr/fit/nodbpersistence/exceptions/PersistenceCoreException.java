package cz.vutbr.fit.nodbpersistence.exceptions;

/**
 * Core exception of nodbpersistence.
 */
public class PersistenceCoreException extends Exception {

    public PersistenceCoreException(String msg) {
        super(msg);
    }

    public PersistenceCoreException(Throwable ex) {
        super(ex);
    }

    public PersistenceCoreException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
