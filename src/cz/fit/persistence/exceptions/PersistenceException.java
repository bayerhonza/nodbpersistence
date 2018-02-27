package cz.fit.persistence.exceptions;

import cz.fit.persistence.core.PersistenceContext;

public class PersistenceException extends RuntimeException {

    public PersistenceException(String msg) {
        super(msg);
    }

    public PersistenceException(Throwable ex) {
        super(ex);
    }

    public PersistenceException(Throwable ex, String msg) {
        super(msg,ex);
    }
}
