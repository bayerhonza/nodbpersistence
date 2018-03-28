package cz.fit.persistence.exceptions;

public class PersistenceException extends RuntimeException {

    public PersistenceException() {

    }

    public PersistenceException(String msg) {
        super(msg);
    }

    public PersistenceException(Throwable ex) {
        super(ex);
    }

    public PersistenceException(String msg,Throwable ex) {
        super(msg,ex);
    }
}
