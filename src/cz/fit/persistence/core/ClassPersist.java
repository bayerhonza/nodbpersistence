package cz.fit.persistence.core;

import cz.fit.persistence.hash.HashHelper;

import java.nio.file.Path;
import java.util.HashMap;

public final class ClassPersist {

    private Integer hashCode;
    private Class<?> klass;
    private Path classDir;

    private HashMap<Integer,Object> classHashMap;

    private boolean pathInitialized;

    ClassPersist(Class<?> klass) {
        hashCode = HashHelper.getHashFromClass(klass);
        this.klass = klass;
        this.classHashMap = new HashMap<>();
        this.pathInitialized = false;
    }

    public Integer getHashCode() {
        return hashCode;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public String getCanonicalName() {
        return klass.getCanonicalName();
    }
}
