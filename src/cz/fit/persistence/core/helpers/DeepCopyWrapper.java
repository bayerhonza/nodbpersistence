package cz.fit.persistence.core.helpers;

import java.io.Serializable;

public class DeepCopyWrapper implements Serializable {

    private Object object;

    public DeepCopyWrapper(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}
