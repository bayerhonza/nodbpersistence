package cz.fit.persistence.core.helpers.exceptions;

public class ConversionException extends Exception {

    public ConversionException(Throwable t) {
        super(t);
    }

    public ConversionException(String msg) {
        super(msg);
    }
}
