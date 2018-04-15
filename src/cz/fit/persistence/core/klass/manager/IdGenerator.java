package cz.fit.persistence.core.klass.manager;

import org.w3c.dom.Attr;

import javax.swing.text.Element;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID generator used for defining unique ID of ojbects. It is
 * unique for each {@link DefaultClassManagerImpl} and its class.
 *
 * It is created by  {@link DefaultClassManagerImpl}.
 */
public class IdGenerator implements Serializable {

    /**
     * Step of generating IDs.
     */
    private static final Integer GENERATOR_STEP = 1;


    /**
     * The value of first <b>free</b> ID.
     */
    private final AtomicInteger currentId = new AtomicInteger();
    /**
     * Attribut of XML element with ID generator valure. Default value of attribute name is defined in
     * {@link cz.fit.persistence.core.PersistenceContext}.
     */
    private final Attr idGenElement;

    public IdGenerator(Attr idGenElement) {
        this.idGenElement = idGenElement;
        currentId.set(1);
    }

    /**
     * Constructor for restoring ID generator from persisted files.
     * @param idGenElement XML attribute of value of generator
     * @param seed value to be set.
     */
    public IdGenerator(Attr idGenElement, Integer seed) {
        this(idGenElement);
        currentId.set(seed);
    }

    /**
     * current free ID and increment ID generator
     * @return free ID number
     */
    public Integer getNextId() {
        Integer retVal = currentId.getAndAdd(GENERATOR_STEP);
        idGenElement.setValue(currentId.toString());
        return retVal;
    }



}
