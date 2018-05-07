package cz.vutbr.fit.nodbpersistence.core.klass.manager;

import cz.vutbr.fit.nodbpersistence.core.PersistenceContext;
import org.w3c.dom.Attr;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ID generator used for defining unique ID of ojbects. It is
 * unique for each {@link DefaultClassManagerImpl} and its class.
 *
 * It is created by  {@link DefaultClassManagerImpl}.
 */
class IdGenerator implements Serializable {

    /**
     * Step of generating IDs.
     */
    private static final Integer GENERATOR_STEP = 1;


    /**
     * The value of first <b>free</b> ID.
     */
    private final AtomicLong currentId = new AtomicLong();
    /**
     * Attribut of XML element with ID generator valure. Default value of attribute name is defined in
     * {@link PersistenceContext}.
     */
    private final Attr idGenElement;

    IdGenerator(Attr idGenElement) {
        this.idGenElement = idGenElement;
        currentId.set(1);
    }

    /**
     * Constructor for restoring ID generator from persisted files.
     * @param idGenElement XML attribute of value of generator
     * @param seed value to be set.
     */
    IdGenerator(Attr idGenElement, Integer seed) {
        this(idGenElement);
        currentId.set(seed);
    }

    /**
     * current free ID and increment ID generator
     * @return free ID number
     */
    Long getNextId() {
        Long retVal = currentId.getAndAdd(GENERATOR_STEP);
        idGenElement.setValue(currentId.toString());
        return retVal;
    }



}
