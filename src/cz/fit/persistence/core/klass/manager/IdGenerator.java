package cz.fit.persistence.core.klass.manager;

import org.w3c.dom.Attr;

import javax.swing.text.Element;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;


// TODO make IdGenerator persisted - serialize
public class IdGenerator implements Serializable {



    private static final Integer GENERATOR_STEP = 1;

    private final AtomicInteger currentId = new AtomicInteger();
    private final Attr idGenElement;

    public IdGenerator(Attr idGenElement) {
        this.idGenElement = idGenElement;
        currentId.set(1);
    }

    public IdGenerator(Attr idGenElement, Integer seed) {
        this(idGenElement);
        currentId.set(seed);
    }

    public Integer getNextId() {
        Integer retVal = currentId.getAndAdd(GENERATOR_STEP);
        idGenElement.setValue(currentId.toString());
        return retVal;
    }

    public Integer getCurrentValue() {return currentId.get();}

    public void setValue(Integer value) {
        currentId.set(value);
    }



}
