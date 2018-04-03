package cz.fit.persistence.core.helpers;

public class HashHelper {

    public static Integer getHashFromClass(Class<?> klass) {
        return klass.getCanonicalName().hashCode();
    }
}
